package com.example.agenda360lite.clients.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.clients.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ClientListState {
    object Loading : ClientListState()
    data class Error(val message: String) : ClientListState()
    data class Success(
        val items: List<com.example.agenda360lite.clients.data.remote.Client>,
        val meta: com.example.agenda360lite.clients.data.remote.Meta
    ) : ClientListState()
}

class ClientListViewModel : ViewModel() {
    private val repo = ClientRepository()

    private val _state = MutableStateFlow<ClientListState>(ClientListState.Loading)
    val state: StateFlow<ClientListState> = _state

    var q: String? = null
    var page: Int = 0
    var size: Int = 20

    fun setQuery(query: String) { q = query }
    fun nextPage() { page += 1 }
    fun prevPage() { page = (page - 1).coerceAtLeast(0) }

    fun load() {
        viewModelScope.launch {
            _state.value = ClientListState.Loading
            try {
                val paged = repo.getPaged(q, page, size)
                _state.value = ClientListState.Success(paged.items, paged.meta)
            } catch (e: Exception) {
                _state.value = ClientListState.Error(e.message ?: "Error")
            }
        }
    }
}
