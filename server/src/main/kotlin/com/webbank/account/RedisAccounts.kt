package com.webbank.account

import java.util.UUID

import redis.clients.jedis.UnifiedJedis

class RedisAccounts(
    private val redis: UnifiedJedis,
    private val ids: () -> String = { UUID.randomUUID().toString() }
) : Accounts {

    override suspend fun save(account: Account): String {
        val id = ids()

        redis.hset("account:$id", account.fields())

        return id
    }

    override suspend fun list(): AccountList {

        return AccountList()
    }
}
