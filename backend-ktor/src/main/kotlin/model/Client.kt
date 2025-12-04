package model

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Long,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
    val ownerId: Long
)

