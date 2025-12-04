package com.example.agenda360lite.core.datastorage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {
    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut

    fun notifyLoggedOut() { _loggedOut.value = true }
    fun reset() { _loggedOut.value = false }
}

