package dev.webbank

import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.http.content.staticResources
import io.ktor.server.webjars.Webjars
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.call
import java.util.UUID

import dev.webbank.json.JsonObject
import dev.webbank.persistence.Repository
import dev.webbank.user.User

fun Application.api(repository: Repository) {
	install(Webjars)
    routing {
		get("/user/{id}") {
			val id = UUID.fromString(call.parameters["id"])
			val user = repository.readUser(id)

			call.respondText(user.toJson().toString())
		}
		post("/user") {
			val json = JsonObject.parse(call.receiveText())
			val user = User.parse(json)

			repository.saveUser(user)

			call.respondText(user.toJson().toString())
		}
        staticResources("/", "static")
    }
}
