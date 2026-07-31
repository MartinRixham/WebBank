package com.webbank

import com.webbank.account.Account
import com.webbank.account.Accounts
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ACCOUNT_JSON = """
    {
        "firstName": "Eleanor",
        "lastName": "Whitmore",
        "email": "e.whitmore@email.com",
        "phone": "+1 (555) 000-0000",
        "dateOfBirth": "01 / 01 / 1990",
        "ssn": "6789"
    }
"""

private class FakeAccounts : Accounts {

    val saved = mutableListOf<Account>()

    override suspend fun save(account: Account): String {
        saved.add(account)

        return "1234"
    }
}

class AccountRouteTest {

    @Test
    fun `saves the account it is posted`() = testApplication {
        val accounts = FakeAccounts()

        application { module(accounts) }

        client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(ACCOUNT_JSON)
        }

        assertEquals(
            listOf(
                Account(
                    "Eleanor",
                    "Whitmore",
                    "e.whitmore@email.com",
                    "+1 (555) 000-0000",
                    "01 / 01 / 1990",
                    "6789"
                )
            ),
            accounts.saved
        )
    }

    @Test
    fun `answers that the account was created`() = testApplication {
        application { module(FakeAccounts()) }

        val response =
            client.post("/account") {
                contentType(ContentType.Application.Json)
                setBody(ACCOUNT_JSON)
            }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `answers with the id the account was saved under`() = testApplication {
        application { module(FakeAccounts()) }

        val response =
            client.post("/account") {
                contentType(ContentType.Application.Json)
                setBody(ACCOUNT_JSON)
            }

        assertEquals("""{"id":"1234"}""", response.bodyAsText())
    }

    @Test
    fun `rejects an account that is missing a detail`() = testApplication {
        val accounts = FakeAccounts()

        application { module(accounts) }

        val response =
            client.post("/account") {
                contentType(ContentType.Application.Json)
                setBody("""{ "firstName": "Eleanor" }""")
            }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(emptyList(), accounts.saved)
    }

    @Test
    fun `rejects a body that is not json`() = testApplication {
        val accounts = FakeAccounts()

        application { module(accounts) }

        val response =
            client.post("/account") {
                contentType(ContentType.Application.Json)
                setBody("Eleanor")
            }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(emptyList(), accounts.saved)
    }
}
