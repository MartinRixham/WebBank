package com.webbank.account

import java.util.UUID

class RedisAccounts(
    private val hashes: Hashes,
    private val ids: () -> String = { UUID.randomUUID().toString() }
) : Accounts {

    override suspend fun save(account: Account): String {
        val id = ids()

        hashes.set("account:$id", account.fields())

        return id
    }
}
