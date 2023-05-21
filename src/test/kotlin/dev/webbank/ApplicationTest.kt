package dev.webbank

import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.http.HttpStatusCode

import dev.webbank.api
import dev.webbank.persistence.Repository

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
		val repository = Repository()

        application {
            api(repository)
        }

        val response = client.post("/user")

		assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }
}
