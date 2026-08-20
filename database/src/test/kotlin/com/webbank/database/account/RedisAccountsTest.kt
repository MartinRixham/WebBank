package com.webbank.database.account

import kotlinx.coroutines.test.runTest
import redis.clients.jedis.UnifiedJedis
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeRedis : UnifiedJedis() {

    val hashes = mutableMapOf<String, Map<String, String>>()

    override fun hset(key: String, hash: Map<String, String>): Long {
        hashes[key] = hash

        return hash.size.toLong()
    }
}

class RedisAccountsTest {

    private val account =
        Account(
            firstName = "Eleanor",
            lastName = "Whitmore",
            email = "e.whitmore@email.com",
            phone = "+1 (555) 000-0000",
            dateOfBirth = "01 / 01 / 1990",
            ssn = "6789"
        )

    @Test
    fun `saves an account as a hash keyed by its id`() = runTest {
        val redis = FakeRedis()

        RedisAccounts(redis, ids = { "1234" }).save(account)

        assertEquals(account.fields(), redis.hashes["account:1234"])
    }

    @Test
    fun `answers with the id it saved the account under`() = runTest {
        val id = RedisAccounts(FakeRedis(), ids = { "1234" }).save(account)

        assertEquals("1234", id)
    }

    @Test
    fun `gives each account an id of its own`() = runTest {
        val ids = listOf("1234", "5678").iterator()
        val redis = FakeRedis()
        val accounts = RedisAccounts(redis, ids = { ids.next() })

        accounts.save(account)
        accounts.save(account)

        assertEquals(setOf("account:1234", "account:5678"), redis.hashes.keys)
    }

    @Test
    fun `saves an account whose details are empty`() = runTest {
        val redis = FakeRedis()

        RedisAccounts(redis, ids = { "1234" }).save(Account("", "", "", "", "", ""))

        assertEquals("", redis.hashes["account:1234"]?.get("firstName"))
    }

    @Test
    fun `lists no accounts`() = runTest {
        val accounts = RedisAccounts(FakeRedis())

        assertEquals(AccountList(), accounts.list())
    }
}
