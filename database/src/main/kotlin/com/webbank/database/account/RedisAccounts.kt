package com.webbank.database.account

import java.util.UUID

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import redis.clients.jedis.UnifiedJedis

class RedisAccounts(
    private val redis: UnifiedJedis,
    private val ids: () -> String = { UUID.randomUUID().toString() }
) : Accounts {

    override suspend fun save(account: Account): String {
        val id = ids()

        // Jedis commands block the calling thread, so they are kept off the
        // event loop that is answering the server's request.
        withContext(Dispatchers.IO) {
            redis.hset("account:$id", account.fields())
        }

        return id
    }

    override suspend fun list(): AccountList {

        return AccountList()
    }
}
