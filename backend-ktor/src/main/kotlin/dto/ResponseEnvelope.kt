package dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponseEnvelope<T>(
    val data: T? = null,
    val error: String? = null,
    val message: String = "OK"
)

