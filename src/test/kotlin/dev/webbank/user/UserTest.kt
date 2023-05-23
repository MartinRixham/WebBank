package dev.webbank.user

import kotlin.test.Test
import kotlin.test.assertEquals

import dev.webbank.json.JsonObject
import dev.webbank.json.JsonValue

class UserTest {

	@Test
	fun userHasName() {

		val json = JsonObject().put("name", "a user name").read()
		val user = User.parse(json)

		assertEquals("a user name", user.toJson().read().getString("name"))
	}
}
