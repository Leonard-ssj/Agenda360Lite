package com.example

import auth.AuthService
import auth.DbUserRepository
import dto.LoginRequest
import dto.LoginResponse
import dto.RegisterRequest
import dto.ResponseEnvelope
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
            route("/api/v1") {
                route("/auth") {
                    post("/login") {
                        val req = call.receive<LoginRequest>()
                        val service = AuthService(DbUserRepository())
                        val result = service.login(req.email, req.password)
                        if (result == null) {
                            call.respond(HttpStatusCode.Unauthorized, ResponseEnvelope<Unit>(data = null, error = "INVALID_CREDENTIALS", message = "Unauthorized"))
                        } else {
                            val (token, user) = result
                            call.respond(
                                HttpStatusCode.OK,
                                ResponseEnvelope(LoginResponse(token, dto.UserInfo(user.id, user.name, user.email)))
                            )
                        }
                    }
                    post("/register") {
                        val req = call.receive<RegisterRequest>()
                        val repo = DbUserRepository()
                        if (req.name.isBlank() || req.email.isBlank() || req.password.length < 6) {
                            return@post call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid fields"))
                        }
                        if (repo.findByEmail(req.email) != null) {
                            return@post call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "EMAIL_IN_USE", "Email already registered"))
                        }
                        val hash = org.mindrot.jbcrypt.BCrypt.hashpw(req.password, org.mindrot.jbcrypt.BCrypt.gensalt())
                        val user = repo.create(req.name, req.email, hash)
                        call.respond(HttpStatusCode.Created, ResponseEnvelope(dto.UserInfo(user.id, user.name, user.email)))
                    }
                }
            }
    }
}
