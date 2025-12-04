package com.example.agenda360lite.appointments.data.repository

import com.example.agenda360lite.appointments.data.remote.AppointmentApi
import com.example.agenda360lite.appointments.data.remote.Appointment
import com.example.agenda360lite.appointments.data.remote.StatusRequest
import com.example.agenda360lite.appointments.data.remote.CreateAppointmentRequest
import com.example.agenda360lite.core.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository {
    private val api = RetrofitClient.instance.create(AppointmentApi::class.java)

    suspend fun getByDate(date: String): List<Appointment> = withContext(Dispatchers.IO) {
        val res = api.getByDate(date)
        res.data ?: emptyList()
    }

    suspend fun getById(id: Long): Appointment? = withContext(Dispatchers.IO) {
        api.getById(id).data
    }

    suspend fun updateStatus(id: Long, status: String): Appointment? = withContext(Dispatchers.IO) {
        api.updateStatus(id, StatusRequest(status)).data
    }

    suspend fun create(body: CreateAppointmentRequest): Appointment? = withContext(Dispatchers.IO) {
        api.create(body).data
    }
}
