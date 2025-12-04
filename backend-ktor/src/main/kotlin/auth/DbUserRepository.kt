package auth

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Users

class DbUserRepository : UserRepository {
    override fun findByEmail(email: String): User? = transaction {
        Users.select { Users.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let {
                User(
                    id = it[Users.id],
                    name = it[Users.name],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash]
                )
            }
    }

    fun create(name: String, email: String, passwordHash: String): User = transaction {
        val now = java.time.LocalDateTime.now()
        val id = Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.role] = "USER"
            it[Users.createdAt] = now
            it[Users.updatedAt] = now
        } get Users.id
        User(id = id, name = name, email = email, passwordHash = passwordHash)
    }
}
