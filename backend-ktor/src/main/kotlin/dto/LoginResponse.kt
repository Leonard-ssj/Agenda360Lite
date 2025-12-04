package dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserInfo
)

@Serializable
data class UserInfo(
    val id: Long,
    val name: String,
    val email: String
)

