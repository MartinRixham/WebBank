# Scans

Reading records in key order. This is what an ordered store is for, and it is
the part of the API worth designing carefully.

```http
GET /table/account/key?prefix=user:&limit=100
```

```json
{
  "records": [
    { "key": "user:4821", "value": {"firstName": "Eleanor"} },
    { "key": "user:7203", "value": {"firstName": "Marcus"} }
  ],
  "next": "eyJrIjoidXNlcjo3MjAzIiwicyI6NDIxOTl9"
}
```

`records` is in key order. `next` is a cursor, and its absence means the range
is exhausted.

## The range

| Parameter | Means |
| --- | --- |
| `prefix` | Every key beginning with this. Shorthand for a `from`/`to` pair |
| `from` | Start here, **inclusive** |
| `to` | Stop here, **exclusive** |
| `reverse` | `true` walks the range from `to` back towards `from` |
| `limit` | At most this many records. Default 100, maximum 1000 |
| `values` | `false` returns keys only |

`from` inclusive and `to` exclusive is RocksDB's own convention, and it is the
one that makes ranges compose: the `to` of one page is the `from` of the next
with nothing dropped and nothing repeated.

Omit both bounds and the scan is the whole table, which is a legitimate thing to
ask for and an expensive one.

`values=false` is not a cosmetic saving. It lets the service iterate without
fetching values, which for a table of large values is the difference between
reading index blocks and reading the table.

With `reverse=true` the bounds keep their meaning — `from` is still the low key
and `to` still the high one — and only the direction of travel changes. This is
worth stating because the alternative, swapping the bounds' meaning with the
direction, is how clients end up scanning an empty range and believing the table
is empty.

## Paging, and what a cursor promises

A cursor encodes the last key returned. Passing it resumes strictly after that
key:

```http
GET /table/account/key?prefix=user:&cursor=eyJrIjoidXNlcjo3MjAzIiwicyI6NDIxOTl9
```

The cursor is opaque. It is not a key, and a client that decodes one and builds
its own has built a `from`, which it could have asked for honestly.

**A paged scan is not a consistent read.** Each page is a new RocksDB iterator,
and an iterator sees the instance as it was when it was created. Records written
between two pages are visible to the second page and not the first. So:

- A record inserted behind the cursor is missed.
- A record inserted ahead of it appears, even though it did not exist when the
  scan began.
- A record deleted ahead of the cursor is gone, though an earlier page might
  have promised it.

Within one page the view is consistent, because one iterator serves it. Across
pages it is not, unless the scan runs against a snapshot.

## Snapshots

A snapshot is the instance at a point in time. Reads that name one see exactly
that, however long they take and whatever is written meanwhile.

```http
POST /snapshot
```

```json
{ "id": "s-42199", "createdAt": "2026-08-20T09:14:03Z", "expiresAt": "2026-08-20T09:19:03Z" }
```

```http
GET /table/account/key?prefix=user:&snapshot=s-42199
```

Any read takes `snapshot`, not only a scan: reading one record against the same
snapshot as a scan is how a client follows a reference it found and knows the
two agree.

A cursor returned by a scan against a snapshot carries the snapshot with it, so
paging stays consistent without the client repeating the parameter.

### Snapshots expire, and that is not a convenience

A snapshot stops RocksDB from discarding any version of any record that the
snapshot can still see, and stops compaction from dropping the files that hold
them. A snapshot held open for an hour on a busy instance costs disk in
proportion to what was written during that hour.

So a snapshot has a five minute lease. `POST /snapshot/{id}/renew` extends it by
another five minutes, and `DELETE /snapshot/{id}` releases it, which a client
that has finished should always do. A read against an expired snapshot returns
`410 Gone` with `snapshot_expired` — a hard error rather than a silent fall back
to the live view, because falling back would answer a question about the past
with a fact about the present.

The instance refuses to hold more than a fixed number of snapshots at once
(`503 too_many_snapshots`). One client leaking snapshots in a loop should fail
rather than fill the disk.

## Scans and the shape of keys

The API cannot filter on anything but the key, so what a scan can answer is
decided when the keys are designed, not when the query is written. Two rules
carry most of it:

- **Put in the key, in order, what you will want to scan by.** A transaction
  keyed `{account}\0{timestamp}` answers "this account's transactions, newest
  first" with one reverse scan. Keyed `{timestamp}\0{account}` it answers "every
  account's transactions in time order" instead, and answers the first question
  only by reading everything.
- **A prefix scan is only cheap if the prefix is a prefix.** Asking for keys
  *containing* something is a full scan with the service throwing most of it
  away, which is why the API does not offer it: it would look like a query and
  cost like a table scan.

Where both orders are genuinely needed, the answer is a second table holding the
other key order, written in the same [batch](/database/batches) as the first.
That is a secondary index, built explicitly, with its cost visible at the point
where it is paid.
