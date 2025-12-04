package com.example.agenda360lite.core.datastorage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private lateinit var appContext: Context
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val KEY_TOKEN = stringPreferencesKey("jwtToken")
    private val KEY_NAME = stringPreferencesKey("userName")
    private val KEY_EMAIL = stringPreferencesKey("userEmail")

    var token: String? = null
        private set
    var userName: String? = null
        private set
    var userEmail: String? = null
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        scope.launch {
            val prefs = appContext.dataStore.data.first()
            token = prefs[KEY_TOKEN]
            userName = prefs[KEY_NAME]
            userEmail = prefs[KEY_EMAIL]
        }
    }

    fun saveAuthData(jwtToken: String, userName: String, userEmail: String) {
        token = jwtToken
        this.userName = userName
        this.userEmail = userEmail
        scope.launch {
            appContext.dataStore.edit { prefs ->
                prefs[KEY_TOKEN] = jwtToken
                prefs[KEY_NAME] = userName
                prefs[KEY_EMAIL] = userEmail
            }
        }
    }

    fun clearAuth() {
        token = null
        scope.launch {
            appContext.dataStore.edit { prefs ->
                prefs.remove(KEY_TOKEN)
                prefs.remove(KEY_NAME)
                prefs.remove(KEY_EMAIL)
            }
        }
    }
}
