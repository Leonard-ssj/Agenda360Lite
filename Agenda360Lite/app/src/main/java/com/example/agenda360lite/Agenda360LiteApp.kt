package com.example.agenda360lite

import android.app.Application
import com.example.agenda360lite.core.datastorage.UserPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Agenda360LiteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UserPreferences.init(this)
    }
}
