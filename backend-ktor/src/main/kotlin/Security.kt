package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import env.Env
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = Env.get("JWT_AUDIENCE", "agenda360-audience")!!
    val jwtDomain = Env.get("JWT_ISSUER", "agenda360")!!
    val jwtRealm = "Agenda360 Lite"
    val jwtSecret = Env.get("JWT_SECRET", "change_me")!!
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
