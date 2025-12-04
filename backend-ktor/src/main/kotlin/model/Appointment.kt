package model

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: Long,
    val clientId: Long,
    val serviceId: Long,
    val userId: Long,
    val dateTime: String, // ISO 8601 UTC string
    val status: String, // SCHEDULED, DONE, CANCELLED
    val locationLat: Double? = null,
    val locationLon: Double? = null,
    val photoUrl: String? = null,
    val notes: String? = null
)

