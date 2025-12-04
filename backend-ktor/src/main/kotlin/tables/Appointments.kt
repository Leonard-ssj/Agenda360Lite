package tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Appointments : Table("appointments") {
    val id = long("id").autoIncrement()
    val clientId = long("client_id").index()
    val serviceId = long("service_id").index()
    val userId = long("user_id").index()
    val dateTime = datetime("date_time").index()
    val status = varchar("status", 20)
    val locationLat = double("location_lat").nullable()
    val locationLon = double("location_lon").nullable()
    val photoUrl = varchar("photo_url", 255).nullable()
    val notes = text("notes").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}
