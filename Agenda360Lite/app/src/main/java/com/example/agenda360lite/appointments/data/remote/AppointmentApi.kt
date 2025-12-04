package com.example.agenda360lite.appointments.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentApi {
    @GET("api/v1/appointments")
    suspend fun getByDate(@Query("date") date: String): AppointmentListResponse

    @GET("api/v1/appointments/availability")
    suspend fun getAvailability(@Query("date") date: String, @Query("serviceId") serviceId: Long): AvailabilityResponse

    @POST("api/v1/appointments")
    suspend fun create(@Body body: CreateAppointmentRequest): AppointmentResponse

    @PUT("api/v1/appointments/{id}/status")
    suspend fun updateStatus(@Path("id") id: Long, @Body body: StatusRequest): AppointmentResponse

    @GET("api/v1/appointments/{id}")
    suspend fun getById(@Path("id") id: Long): AppointmentResponse
}

@Serializable
data class AppointmentListResponse(val data: List<Appointment>?, val error: String?, val message: String)

@Serializable
data class AvailabilityResponse(val data: List<String>?, val error: String?, val message: String)

@Serializable
data class AppointmentResponse(val data: Appointment?, val error: String?, val message: String)

@Serializable
data class Appointment(
    val id: Long,
    val clientId: Long,
    val serviceId: Long,
    val userId: Long,
    val dateTime: String,
    val status: String,
    val notes: String? = null
)

@Serializable
data class CreateAppointmentRequest(
    val id: Long = 0,
    val clientId: Long,
    val serviceId: Long,
    val userId: Long = 0,
    val dateTime: String,
    val status: String = "SCHEDULED",
    val notes: String? = null
)

@Serializable
data class StatusRequest(val status: String)
