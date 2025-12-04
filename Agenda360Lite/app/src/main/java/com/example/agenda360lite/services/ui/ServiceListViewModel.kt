package com.example.agenda360lite.services.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.services.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ServiceListState {
    object Loading : ServiceListState()
    data class Error(val message: String) : ServiceListState()
    data class Success(
        val items: List<com.example.agenda360lite.services.data.remote.ServiceItem>,
        val meta: com.example.agenda360lite.services.data.remote.Meta
    ) : ServiceListState()
}

class ServiceListViewModel : ViewModel() {
    private val repo = ServiceRepository()

    private val _state = MutableStateFlow<ServiceListState>(ServiceListState.Loading)
    val state: StateFlow<ServiceListState> = _state

    var q: String? = null
    var page: Int = 0
    var size: Int = 20

    fun setQuery(query: String) { q = query }
    fun nextPage() { page += 1 }
    fun prevPage() { page = (page - 1).coerceAtLeast(0) }

    fun load() {
        viewModelScope.launch {
            _state.value = ServiceListState.Loading
            try {
                val paged = repo.getPaged(q, page, size)
                _state.value = ServiceListState.Success(paged.items, paged.meta)
            } catch (e: Exception) {
                _state.value = ServiceListState.Error(e.message ?: "Error")
            }
        }
    }
}
