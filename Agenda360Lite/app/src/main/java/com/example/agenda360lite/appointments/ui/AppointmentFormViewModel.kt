package com.example.agenda360lite.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.appointments.data.remote.AppointmentApi
import com.example.agenda360lite.appointments.data.remote.CreateAppointmentRequest
import com.example.agenda360lite.core.network.RetrofitClient
import com.example.agenda360lite.clients.data.repository.ClientRepository
import com.example.agenda360lite.services.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppointmentFormState(
    val clients: List<com.example.agenda360lite.clients.data.remote.Client> = emptyList(),
    val services: List<com.example.agenda360lite.services.data.remote.ServiceItem> = emptyList(),
    val slots: List<String> = emptyList(),
    val selectedClientId: Long? = null,
    val selectedServiceId: Long? = null,
    val selectedDate: String = java.time.LocalDate.now(java.time.ZoneOffset.UTC).toString(),
    val selectedSlot: String? = null,
    val loading: Boolean = false,
    val slotsLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AppointmentFormViewModel : ViewModel() {
    private val clientRepo = ClientRepository()
    private val serviceRepo = ServiceRepository()
    private val api = RetrofitClient.instance.create(AppointmentApi::class.java)

    private val _state = MutableStateFlow(AppointmentFormState())
    val state: StateFlow<AppointmentFormState> = _state

    fun init() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val clientsPaged = clientRepo.getPaged(null, 0, 20)
                val servicesPaged = serviceRepo.getPaged(null, 0, 20)
                _state.value = _state.value.copy(loading = false, clients = clientsPaged.items, services = servicesPaged.items)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun selectService(id: Long) {
        if (_state.value.selectedServiceId == id) return
        _state.value = _state.value.copy(selectedServiceId = id, selectedSlot = null)
        refreshSlots()
    }

    fun selectClient(id: Long) {
        _state.value = _state.value.copy(selectedClientId = id)
    }

    fun selectDate(date: String) {
        if (_state.value.selectedDate == date) return
        _state.value = _state.value.copy(selectedDate = date, selectedSlot = null)
        refreshSlots()
    }

    private fun refreshSlots() {
        val s = _state.value.selectedServiceId ?: return
        val d = _state.value.selectedDate
        viewModelScope.launch {
            _state.value = _state.value.copy(slotsLoading = true, error = null)
            try {
                val res = api.getAvailability(d, s)
                _state.value = _state.value.copy(slotsLoading = false, slots = res.data ?: emptyList())
            } catch (e: Exception) {
                _state.value = _state.value.copy(slotsLoading = false, error = e.message)
            }
        }
    }

    fun selectSlot(slot: String) {
        _state.value = _state.value.copy(selectedSlot = slot)
    }

    fun create() {
        val st = _state.value
        val c = st.selectedClientId ?: return
        val s = st.selectedServiceId ?: return
        val slot = st.selectedSlot ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null, success = false)
            try {
                val res = api.create(CreateAppointmentRequest(clientId = c, serviceId = s, dateTime = slot))
                _state.value = _state.value.copy(loading = false, success = res.data != null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun resetSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}
