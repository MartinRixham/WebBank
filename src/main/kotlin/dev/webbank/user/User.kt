package dev.webbank.user

import java.util.UUID

import dev.webbank.persistence.Valid
import dev.webbank.persistence.Invalid

interface User {

	fun toJson(): String

	companion object {

		fun parse(): User {
			val uid = UUID.randomUUID()
			return ValidUser(uid, "name")

		}
	}
}

class ValidUser(private val uid: UUID, private val name: String): User, Valid {

	override fun getId(): UUID {
		return uid;
	}

	override fun toJson(): String {
		return """{ "id": "$uid" }"""
	}
}

class InvalidUser(private val message: String): Invalid {

	override fun getMessage(): String {
		return message;
	}
}
