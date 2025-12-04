package dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateAppointmentRequest(
    val clientId: Long,
    val serviceId: Long,
    val dateTime: String,
    val status: String = "SCHEDULED",
    val notes: String? = null
)

