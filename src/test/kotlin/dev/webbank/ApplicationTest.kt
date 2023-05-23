package dev.webbank

import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.util.UUID

import dev.webbank.api
import dev.webbank.json.JsonObject
import dev.webbank.persistence.Repository
import dev.webbank.persistence.Valid
import dev.webbank.user.ValidUser

class ApplicationTest {

    @Test
    fun `test saving user`() = testApplication {
		val repository = Repository()

        application {
            api(repository)
        }

		val request = JsonObject().put("name", "a user name")

        val response = client.post("/user") {
			setBody(request.toString())
		}

		assertEquals(HttpStatusCode.OK, response.status)

		val json = JsonObject.parse(response.bodyAsText())
		val user = repository.readUser(UUID.fromString((json.getString("id"))))

		assertTrue(user is Valid)
        assertEquals(json.getString("id"), user.getId().toString())
        assertEquals(json.getString("name"), "a user name")
	}

	@Test
	fun `test reading user`() = testApplication {
		val repository = Repository()

        application {
            api(repository)
        }

		val id = UUID.randomUUID()
		val user = ValidUser(id, "a user name")

		repository.saveUser(user)

		val response = client.get("/user/$id")
		val json = JsonObject.parse(response.bodyAsText())

		assertEquals(id.toString(), json.getString("id"))
		assertEquals("a user name", json.getString("name"))

	}
}
