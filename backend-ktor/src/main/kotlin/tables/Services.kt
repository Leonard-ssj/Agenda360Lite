package tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Services : Table("services") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 120)
    val durationMinutes = integer("duration_minutes")
    val price = double("price")
    val description = text("description").nullable()
    val ownerId = long("owner_id").index()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}
