package com.example.agenda360lite.appointments.domain

import com.example.agenda360lite.appointments.data.repository.AppointmentRepository
import java.time.LocalDate
import java.time.ZoneOffset

class GetTodayAppointmentsUseCase(private val repo: AppointmentRepository) {
    suspend fun execute(): List<com.example.agenda360lite.appointments.data.remote.Appointment> {
        val today = LocalDate.now(ZoneOffset.UTC).toString()
        return repo.getByDate(today)
    }
}

