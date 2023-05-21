package dev.webbank

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.http.HttpStatusCode
import dev.webbank.configureRouting

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
