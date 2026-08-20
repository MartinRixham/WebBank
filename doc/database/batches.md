# Batches

Several writes, applied as one. Either every operation in a batch is visible or
none of it is, and no read ever sees half of it.

```http
POST /batch
Content-Type: application/json
Durability: sync

{
  "operations": [
    { "op": "put", "table": "account", "key": "4821", "value": {"balance": 51340.28} },
    { "op": "put", "table": "transaction", "key": "4821:20260820T091403Z", "value": {"amount": -250} },
    { "op": "delete", "table": "pending", "key": "t-99812" }
  ]
}
```

`200 OK` with the number of operations applied. `Durability` works exactly as it
does for a [single write](/database/records#durability), and applies to the
batch as a whole.

The batch spans three tables, and that is the point. RocksDB column families
share one write-ahead log, so a write batch across them is atomic at no extra
cost — no two-phase commit, no coordinator. An API over RocksDB that made the
client write to each table separately would be throwing away the one guarantee
that is free.

`POST /table/{table}/batch` takes the same body with `table` omitted from every
operation, for the common case of a batch within one table.

## Operations

| `op` | Fields | Is |
| --- | --- | --- |
| `put` | `table`, `key`, `value` | The same write as `PUT /table/{table}/key/{key}` |
| `delete` | `table`, `key` | The same delete as `DELETE` on the record |
| `deleteRange` | `table`, `from`, `to` | A range tombstone, as in [delete a range](/database/tables#delete-a-range) |

Values follow the table's declared content type. In a JSON table the `value` is
the JSON itself, inline. In a table declared `application/octet-stream` it is a
base64url string, because there is no other way to put bytes in a JSON document
— which is a fair reason to keep a table of binary values out of batches where
it can be avoided.

Keys follow the request's `X-Key-Encoding`, exactly as they do elsewhere.

## Limits

A batch is at most 1000 operations and 16 MiB. Beyond that the request is
refused with `413`, rather than accepted and applied in pieces, because applying
it in pieces would break the only promise a batch makes.

Bulk loading a table is therefore many batches, and the right durability for
that is `none` — with a `POST /table/{table}/flush` at the end, and the
knowledge that a crash means starting the load again.

## Ordering within a batch

Operations apply in the order given, so two writes to the same key in one batch
leave the last one. This matters mainly for `deleteRange` followed by `put`:
that sequence replaces a range, and the reverse sequence deletes what it just
wrote.

## What a batch is not

**A batch is not conditional.** There is no `If-Match` on a batch and no
operation that fails the batch when a key is missing or has changed. A batch
either applies or the request failed; it does not apply-if.

This is a real limit, and it is where a client's needs will eventually outgrow
this API. Moving money between two accounts wants both writes *and* a check that
the source balance has not moved since it was read — which needs a transaction
held open across two round trips, with the locks that implies. That is a
different resource, with a lease and a rollback, and it is deliberately not in
this design: the cost of holding transactions open over HTTP should be paid
knowingly, once there is a case that needs it, rather than sitting in the API
tempting clients who do not.

Until then, a client that needs a check with its batch does the check with a
[conditional write](/database/records#conditional-writes) on the one key that
matters, and puts the rest in the batch behind it.
