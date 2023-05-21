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

import dev.webbank.persistence.Repository
import dev.webbank.user.User

fun Application.api(repository: Repository) {
	install(Webjars)
    routing {
		post("/user") {
			call.receiveText()

			val user = User.parse()
			repository.saveUser(user)

			call.respondText(user.toJson())
		}
        staticResources("/", "static")
    }
}
