package com.example.agenda360lite.clients.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ClientApi {
    @GET("api/v1/clients")
    suspend fun getPaged(
        @Query("q") q: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): ClientPagedResponse

    @POST("api/v1/clients")
    suspend fun create(@Body body: Client): ClientResponse

    @GET("api/v1/clients/{id}")
    suspend fun getById(@Path("id") id: Long): ClientResponse

    @PUT("api/v1/clients/{id}")
    suspend fun update(@Path("id") id: Long, @Body body: Client): ClientResponse
}

@Serializable
data class ClientPagedResponse(val data: PagedClient?, val error: String?, val message: String)

@Serializable
data class PagedClient(val items: List<Client>, val meta: Meta)

@Serializable
data class Meta(val page: Int, val size: Int, val total: Long)

@Serializable
data class Client(
    val id: Long,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
    val ownerId: Long
)

@Serializable
data class ClientResponse(val data: Client?, val error: String?, val message: String)
