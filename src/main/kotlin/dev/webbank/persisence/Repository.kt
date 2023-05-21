package dev.webbank.persistence

import java.util.UUID
import dev.webbank.user.User

interface Valid {
	fun getId(): UUID
}

interface Invalid {
	fun getMessage(): String
}

class Repository() {

	private val users = mutableMapOf<UUID, User>()

	public fun saveUser(user: User) {
		if (user is Valid) {
			users.set(user.getId(), user)
		}
	}

	public fun readUser(id: UUID): User {
		return users.getValue(id)
	}
}
