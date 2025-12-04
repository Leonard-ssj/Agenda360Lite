package model

import kotlinx.serialization.Serializable

@Serializable
data class ServiceItem(
    val id: Long,
    val name: String,
    val durationMinutes: Int,
    val price: Double,
    val description: String? = null,
    val ownerId: Long
)

