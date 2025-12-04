package com.example.agenda360lite.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.appointments.data.repository.AppointmentRepository
import com.example.agenda360lite.appointments.domain.GetTodayAppointmentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class Error(val message: String) : DashboardState()
    data class Success(val items: List<com.example.agenda360lite.appointments.data.remote.Appointment>) : DashboardState()
}

class DashboardViewModel : ViewModel() {
    private val repo = AppointmentRepository()
    private val useCase = GetTodayAppointmentsUseCase(repo)

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state

    fun loadToday() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            try {
                val items = useCase.execute()
                _state.value = DashboardState.Success(items)
            } catch (e: Exception) {
                _state.value = DashboardState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadDate(date: String) {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            try {
                val items = repo.getByDate(date)
                _state.value = DashboardState.Success(items)
            } catch (e: Exception) {
                _state.value = DashboardState.Error(e.message ?: "Error")
            }
        }
    }
}
