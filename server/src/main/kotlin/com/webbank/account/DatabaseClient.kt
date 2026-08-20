package com.webbank.account

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

// One client for the life of the process, so that calls to the database share a
// pool of kept alive connections instead of paying a TCP handshake each time.
// The engine is Java rather than CIO because CIO gives a request a dedicated
// connection, closed as soon as the response is read, unless it is a GET or a
// HEAD on a pipelined client (see requiresDedicatedConnection); every POST to
// /account would open a socket of its own. The JDK client pools by authority
// for any method, and its sendAsync is awaited rather than blocked on, so a
// request waiting on the store still occupies no thread.
fun databaseClient(): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            json()
        }
        expectSuccess = true
    }
