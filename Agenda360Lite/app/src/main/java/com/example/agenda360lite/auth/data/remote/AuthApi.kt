package com.example.agenda360lite.auth.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val data: Data?, val error: String?, val message: String)

@Serializable
data class Data(val token: String, val user: User)

@Serializable
data class User(val id: Long, val name: String, val email: String)

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)

@Serializable
data class RegisterResponse(val data: User?, val error: String?, val message: String)
