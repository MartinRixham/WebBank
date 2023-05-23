package dev.webbank.user

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

import dev.webbank.json.JsonObject
import dev.webbank.json.JsonValue
import dev.webbank.persistence.Valid

class UserTest {

	@Test
	fun `user has name`() {

		val json = JsonObject().put("name", "a user name").read()
		val user = User.parse(json)

		assertTrue(user is Valid)
		assertNotNull(user.getId())
		assertEquals("a user name", user.toJson().read().getString("name"))
	}
}
