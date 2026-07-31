package com.webbank

import com.webbank.account.Account
import com.webbank.account.AccountCreated
import com.webbank.account.Accounts
import com.webbank.account.JedisHashes
import com.webbank.account.RedisAccounts
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
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import redis.clients.jedis.JedisPooled
import java.net.URI

fun main() {
    // Docker compose runs Redis on its default port; the variable is there for
    // a deployment that puts it somewhere else.
    val redis = JedisPooled(URI(System.getenv("REDIS_URL") ?: "redis://localhost:6379"))

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(RedisAccounts(JedisHashes(redis)))
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

        // The client module packages the static site under web/ on the classpath.
        staticResources("/", "web")
    }
}
