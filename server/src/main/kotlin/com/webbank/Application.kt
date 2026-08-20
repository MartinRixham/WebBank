package com.webbank

import com.webbank.account.Account
import com.webbank.account.AccountCreated
import com.webbank.account.AccountList
import com.webbank.account.Accounts
import com.webbank.account.HttpAccounts
import com.webbank.account.databaseClient
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun main() {
    val database = System.getenv("DATABASE_URL") ?: "http://localhost:8081"

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(HttpAccounts(databaseClient(), database))
    }.start(wait = true)
}

fun Application.module(accounts: Accounts) {
    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    routing {
        post("/account") {
            val id = accounts.save(call.receive<Account>())

            call.respond(HttpStatusCode.Created, AccountCreated(id))
        }

        get("/accounts") {
            call.respond(HttpStatusCode.OK, accounts.list())
        }

        staticResources("/", "web")
    }
}
