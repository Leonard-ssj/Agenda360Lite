package com.example.agenda360lite.auth.domain

import com.example.agenda360lite.auth.data.repository.AuthRepository

class LoginUseCase(private val repo: AuthRepository) {
    suspend fun execute(email: String, password: String): Boolean = repo.login(email, password)
}

