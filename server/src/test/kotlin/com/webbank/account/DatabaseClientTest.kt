package com.webbank.account

import java.io.IOException
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

// Ktor gives no view of the connections underneath a client, so the database is
// answered here by a plain socket server that counts the connections it accepts.
// It speaks just enough HTTP/1.1 to keep one connection open for several
// requests, which is the behaviour the pooling is being held to.
class CountingDatabase {

    private val socket = ServerSocket(0)

    val connections = AtomicInteger()

    val url = "http://localhost:${socket.localPort}"

    init {
        thread(isDaemon = true) {
            while (true) {
                val accepted =
                    try {
                        socket.accept()
                    } catch (_: IOException) {
                        break
                    }

                connections.incrementAndGet()

                thread(isDaemon = true) { serve(accepted) }
            }
        }
    }

    fun close() = socket.close()

    private fun serve(connection: Socket) = connection.use {
        while (true) {
            val request = readRequest(connection.getInputStream()) ?: break

            val body =
                if (request.startsWith("POST")) """{"id":"1234"}"""
                else """{"accounts":[]}"""

            connection.getOutputStream().write(
                (
                    "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: ${body.length}\r\n" +
                        "\r\n" +
                        body
                    ).toByteArray())
            connection.getOutputStream().flush()
        }
    }

    private fun readRequest(input: InputStream): String? {
        val request = readLine(input) ?: return null
        var length = 0

        while (true) {
            val header = readLine(input) ?: return null
            if (header.isEmpty()) break

            if (header.startsWith("Content-Length:", ignoreCase = true)) {
                length = header.substringAfter(":").trim().toInt()
            }
        }

        repeat(length) { if (input.read() < 0) return null }

        return request
    }

    private fun readLine(input: InputStream): String? {
        val line = StringBuilder()

        while (true) {
            when (val character = input.read()) {
                -1 -> return null
                '\n'.code -> return line.toString().removeSuffix("\r")
                else -> line.append(character.toChar())
            }
        }
    }
}

class DatabaseClientTest {

    private val database = CountingDatabase()

    private val client = databaseClient()

    private val accounts = HttpAccounts(client, database.url)

    private val account =
        Account(
            firstName = "Eleanor",
            lastName = "Whitmore",
            email = "e.whitmore@email.com",
            phone = "+1 (555) 000-0000",
            dateOfBirth = "01 / 01 / 1990",
            ssn = "6789")

    @AfterTest
    fun close() {
        client.close()
        database.close()
    }

    @Test
    fun `opens one connection for a single call`() = runBlocking {
        accounts.list()

        assertEquals(1, database.connections.get())
    }

    @Test
    fun `reuses the connection across calls`() = runBlocking {
        repeat(5) { accounts.list() }

        assertEquals(1, database.connections.get())
    }

    @Test
    fun `reuses the connection across calls that carry a body`() = runBlocking {
        repeat(5) { accounts.save(account) }

        assertEquals(1, database.connections.get())
    }

    @Test
    fun `answers every call made at once`() = runBlocking {
        val listed = (1..20).map { async { accounts.list() } }.awaitAll()

        assertEquals(List(20) { AccountList() }, listed)
    }

    @Test
    fun `opens no more connections than the pool allows`() = runBlocking {
        (1..20).map { async { accounts.list() } }.awaitAll()

        assert(database.connections.get() <= 64) {
            "opened ${database.connections.get()} connections"
        }
    }
}
