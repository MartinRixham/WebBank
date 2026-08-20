package com.webbank.account

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

// The database is stood up as a Ktor test application so that the accounts are
// exercised over a real HTTP round trip rather than a stubbed client.
class FakeDatabase {

    val saved = mutableListOf<Account>()

    var accounts = AccountList()

    var status = HttpStatusCode.Created

    fun install(builder: ApplicationTestBuilder) = builder.application {
        install(ServerContentNegotiation) {
            json()
        }

        routing {
            post("/account") {
                saved.add(call.receive<Account>())

                call.respond(status, AccountCreated("1234"))
            }

            get("/accounts") {
                call.respond(HttpStatusCode.OK, accounts)
            }
        }
    }
}

class HttpAccountsTest {

    private val account =
        Account(
            firstName = "Eleanor",
            lastName = "Whitmore",
            email = "e.whitmore@email.com",
            phone = "+1 (555) 000-0000",
            dateOfBirth = "01 / 01 / 1990",
            ssn = "6789")

    private fun ApplicationTestBuilder.accounts(database: FakeDatabase): HttpAccounts {
        database.install(this)

        val client =
            createClient {
                install(ContentNegotiation) {
                    json()
                }
                expectSuccess = true
            }

        return HttpAccounts(client, "")
    }

    @Test
    fun `posts the account to the database`() = testApplication {
        val database = FakeDatabase()

        accounts(database).save(account)

        assertEquals(listOf(account), database.saved)
    }

    @Test
    fun `answers with the id the database created`() = testApplication {
        val id = accounts(FakeDatabase()).save(account)

        assertEquals("1234", id)
    }

    @Test
    fun `fails when the database rejects the account`() = testApplication {
        val database = FakeDatabase()
        database.status = HttpStatusCode.BadRequest

        val accounts = accounts(database)

        assertFailsWith<ClientRequestException> { accounts.save(account) }
    }

    @Test
    fun `lists no accounts when the database holds none`() = testApplication {
        val listed = accounts(FakeDatabase()).list()

        assertEquals(AccountList(), listed)
    }

    @Test
    fun `lists the accounts the database holds`() = testApplication {
        val database = FakeDatabase()
        database.accounts = AccountList(listOf(account))

        val listed = accounts(database).list()

        assertEquals(AccountList(listOf(account)), listed)
    }
}
