package com.example.agenda360lite.auth.data.repository

import com.example.agenda360lite.auth.data.remote.AuthApi
import com.example.agenda360lite.auth.data.remote.LoginRequest
import com.example.agenda360lite.auth.data.remote.RegisterRequest
import com.example.agenda360lite.core.datastorage.UserPreferences
import com.example.agenda360lite.core.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class AuthRepository {
    private val api = RetrofitClient.instance.create(AuthApi::class.java)

    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val res = api.login(LoginRequest(email, password))
            val token = res.data?.token ?: return@withContext false
            val user = res.data.user
            UserPreferences.saveAuthData(token, user.name, user.email)
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "login failed", e)
            false
        }
    }

    suspend fun register(name: String, email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val res = api.register(RegisterRequest(name, email, password))
            res.data != null
        } catch (e: Exception) {
            Log.e("AuthRepository", "register failed", e)
            false
        }
    }
}
