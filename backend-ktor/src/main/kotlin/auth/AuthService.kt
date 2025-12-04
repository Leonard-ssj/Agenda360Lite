package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import env.Env
import org.mindrot.jbcrypt.BCrypt

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val passwordHash: String
)

interface UserRepository {
    fun findByEmail(email: String): User?
}

class InMemoryUserRepository : UserRepository {
    private val users = listOf(
        User(1L, "Demo User", "test@demo.com", "pass")
    )
    override fun findByEmail(email: String): User? = users.find { it.email == email }
}

class AuthService(
    private val repo: UserRepository
) {
    private val issuer = Env.get("JWT_ISSUER", "agenda360")!!
    private val audience = Env.get("JWT_AUDIENCE", "agenda360-audience")!!
    private val secret = Env.get("JWT_SECRET", "change_me")!!
    private val algorithm = Algorithm.HMAC256(secret)

    fun login(email: String, password: String): Pair<String, User>? {
        val user = repo.findByEmail(email) ?: return null
        if (!BCrypt.checkpw(password, user.passwordHash)) return null
        val expMinutes = 60L
        val expMillis = System.currentTimeMillis() + expMinutes * 60_000
        val token = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("sub", user.id)
            .withClaim("email", user.email)
            .withExpiresAt(java.util.Date(expMillis))
            .sign(algorithm)
        return token to user
    }
}
