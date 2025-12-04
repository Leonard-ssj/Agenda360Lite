package utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.userIdFromJwt(): Long? {
    val principal = this.principal<JWTPrincipal>()
    return principal?.getClaim("sub", Long::class)
}

