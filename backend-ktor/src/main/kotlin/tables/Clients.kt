package tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Clients : Table("clients") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 120)
    val phone = varchar("phone", 30).nullable()
    val email = varchar("email", 150).nullable()
    val notes = text("notes").nullable()
    val ownerId = long("owner_id").index()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}
