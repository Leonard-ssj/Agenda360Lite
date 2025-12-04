package seed

import env.Env
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import tables.Users

fun seedDb() {
    val enabled = Env.get("DB_SEED", "false") == "true"
    if (!enabled) return
    transaction {
        val email = "test@demo.com"
        val exists = Users.select { Users.email eq email }.limit(1).any()
        if (!exists) {
            val hash = BCrypt.hashpw("pass", BCrypt.gensalt())
            Users.insertIgnore {
                it[name] = "Demo User"
                it[Users.email] = email
                it[passwordHash] = hash
                it[role] = "USER"
                it[createdAt] = java.time.LocalDateTime.now()
                it[updatedAt] = java.time.LocalDateTime.now()
            }
        }
    }
}
