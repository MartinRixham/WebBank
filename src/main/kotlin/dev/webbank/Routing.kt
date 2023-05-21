package dev.webbank

import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.response.respondText
import io.ktor.server.http.content.staticResources
import io.ktor.server.webjars.Webjars
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.call

fun Application.configureRouting() {
	install(Webjars)
    routing {
        get("/hello") {
            call.respondText("Hello World!")
        }
        staticResources("/", "static")
    }
}
