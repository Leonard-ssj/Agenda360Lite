package dto

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val page: Int,
    val size: Int,
    val total: Long
)

@Serializable
data class Paged<T>(
    val items: List<T>,
    val meta: Meta
)
