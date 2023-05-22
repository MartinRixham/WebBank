package dev.webbank

import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.ktor.http.HttpStatusCode
import java.util.UUID

import dev.webbank.api
import dev.webbank.json.JsonObject
import dev.webbank.persistence.Repository
import dev.webbank.persistence.Valid

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
		val repository = Repository()

        application {
            api(repository)
        }

        val response = client.post("/user")

		assertEquals(HttpStatusCode.OK, response.status)

		val json = JsonObject.parse(response.bodyAsText())
		val user = repository.readUser(UUID.fromString((json.getString("id"))))

		assertTrue(user is Valid)
        assertEquals(json.getString("id").toString(), user.getId().toString())
	}
}
