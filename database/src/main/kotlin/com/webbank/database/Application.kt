package com.webbank.database

import com.webbank.database.account.Account
import com.webbank.database.account.AccountCreated
import com.webbank.database.account.Accounts
import com.webbank.database.account.RedisAccounts
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import redis.clients.jedis.RedisClient
import java.net.URI

fun main() {
    val redis: RedisClient = RedisClient.create(URI(System.getenv("REDIS_URL") ?: "redis://localhost:6379"))
    val port = System.getenv("DATABASE_PORT")?.toInt() ?: 8081

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(RedisAccounts(redis))
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
    }
}
