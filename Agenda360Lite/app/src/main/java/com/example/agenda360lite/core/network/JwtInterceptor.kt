package com.example.agenda360lite.core.network

import com.example.agenda360lite.core.datastorage.UserPreferences
import com.example.agenda360lite.core.datastorage.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = UserPreferences.token
        val req = if (!token.isNullOrEmpty()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else original
        val res = chain.proceed(req)
        if (res.code == 401) {
            UserPreferences.clearAuth()
            SessionManager.notifyLoggedOut()
        }
        return res
    }
}
