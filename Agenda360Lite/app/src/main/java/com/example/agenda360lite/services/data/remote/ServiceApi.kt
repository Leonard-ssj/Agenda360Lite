package com.example.agenda360lite.services.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {
    @GET("api/v1/services")
    suspend fun getPaged(
        @Query("q") q: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): ServicePagedResponse

    @POST("api/v1/services")
    suspend fun create(@Body body: ServiceItem): ServiceResponse

    @GET("api/v1/services/{id}")
    suspend fun getById(@Path("id") id: Long): ServiceResponse

    @PUT("api/v1/services/{id}")
    suspend fun update(@Path("id") id: Long, @Body body: ServiceItem): ServiceResponse
}

@Serializable
data class ServicePagedResponse(val data: PagedService?, val error: String?, val message: String)

@Serializable
data class PagedService(val items: List<ServiceItem>, val meta: Meta)

@Serializable
data class Meta(val page: Int, val size: Int, val total: Long)

@Serializable
data class ServiceItem(
    val id: Long,
    val name: String,
    val durationMinutes: Int,
    val price: Double,
    val description: String? = null,
    val ownerId: Long
)

@Serializable
data class ServiceResponse(val data: ServiceItem?, val error: String?, val message: String)
