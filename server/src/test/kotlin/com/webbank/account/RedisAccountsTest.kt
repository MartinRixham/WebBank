package com.webbank.account

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val ACCOUNT =
    Account(
        firstName = "Eleanor",
        lastName = "Whitmore",
        email = "e.whitmore@email.com",
        phone = "+1 (555) 000-0000",
        dateOfBirth = "01 / 01 / 1990",
        ssn = "6789"
    )

private class FakeHashes : Hashes {

    val written = mutableMapOf<String, Map<String, String>>()

    override suspend fun set(key: String, fields: Map<String, String>) {
        written[key] = fields
    }
}

class RedisAccountsTest {

    @Test
    fun `stores the account under its own key`() = runTest {
        val hashes = FakeHashes()

        RedisAccounts(hashes) { "1234" }.save(ACCOUNT)

        assertEquals(setOf("account:1234"), hashes.written.keys)
    }

    @Test
    fun `stores every detail of the account`() = runTest {
        val hashes = FakeHashes()

        RedisAccounts(hashes) { "1234" }.save(ACCOUNT)

        assertEquals(ACCOUNT.fields(), hashes.written["account:1234"])
    }

    @Test
    fun `answers with the id it stored the account under`() = runTest {
        assertEquals("1234", RedisAccounts(FakeHashes()) { "1234" }.save(ACCOUNT))
    }

    @Test
    fun `gives each account an id of its own`() = runTest {
        val accounts = RedisAccounts(FakeHashes())

        assertEquals(2, setOf(accounts.save(ACCOUNT), accounts.save(ACCOUNT)).size)
    }
}
