package dev.webbank.user

import java.util.UUID

import dev.webbank.json.JsonObject
import dev.webbank.persistence.Valid
import dev.webbank.persistence.Invalid

interface User {

	fun toJson(): JsonObject.Write

	companion object {

		fun parse(json: JsonObject.Read): User {
			val uid = UUID.randomUUID()
			return ValidUser(uid, json.getString("name"))
		}
	}
}

class ValidUser(private val uid: UUID, private val name: String): User, Valid {

	override fun getId(): UUID {
		return uid;
	}

	override fun toJson(): JsonObject.Write {
		return JsonObject()
			.put("id", uid.toString())
			.put("name", name)
	}
}

class InvalidUser(private val message: String): Invalid {

	override fun getMessage(): String {
		return message;
	}
}
