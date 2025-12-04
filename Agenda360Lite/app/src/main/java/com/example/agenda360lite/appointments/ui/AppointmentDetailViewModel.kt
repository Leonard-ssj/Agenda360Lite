package com.example.agenda360lite.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.appointments.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AppointmentDetailState {
    object Loading : AppointmentDetailState()
    data class Error(val message: String) : AppointmentDetailState()
    data class Success(val item: com.example.agenda360lite.appointments.data.remote.Appointment) : AppointmentDetailState()
}

class AppointmentDetailViewModel : ViewModel() {
    private val repo = AppointmentRepository()

    private val _state = MutableStateFlow<AppointmentDetailState>(AppointmentDetailState.Loading)
    val state: StateFlow<AppointmentDetailState> = _state

    fun load(id: Long) {
        viewModelScope.launch {
            _state.value = AppointmentDetailState.Loading
            try {
                val item = repo.getById(id) ?: throw IllegalStateException("Not found")
                _state.value = AppointmentDetailState.Success(item)
            } catch (e: Exception) {
                _state.value = AppointmentDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun setStatus(id: Long, status: String) {
        viewModelScope.launch {
            try {
                val item = repo.updateStatus(id, status) ?: throw IllegalStateException("Update failed")
                _state.value = AppointmentDetailState.Success(item)
            } catch (e: Exception) {
                _state.value = AppointmentDetailState.Error(e.message ?: "Error")
            }
        }
    }
}
