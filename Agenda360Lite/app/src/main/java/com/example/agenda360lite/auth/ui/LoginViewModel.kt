package com.example.agenda360lite.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenda360lite.auth.data.repository.AuthRepository
import com.example.agenda360lite.auth.domain.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String) : UiState()
}

class LoginViewModel : ViewModel() {
    private val repo = AuthRepository()
    private val useCase = LoginUseCase(repo)

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun onLoginClick(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val ok = useCase.execute(email, password)
                _state.value = if (ok) UiState.Success else UiState.Error("Credenciales inválidas")
                if (ok) onSuccess()
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error de red")
            }
        }
    }
}
