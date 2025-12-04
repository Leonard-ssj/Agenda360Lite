package com.example.agenda360lite.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenda360lite.core.datastorage.UserPreferences
import com.example.agenda360lite.core.datastorage.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {
    Scaffold(topBar = { TopAppBar(title = { Text("Perfil") }, navigationIcon = { Button(onClick = { navController?.popBackStack() }) { Text("Atrás") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Usuario: ${UserPreferences.userName ?: ""}")
            Text("Email: ${UserPreferences.userEmail ?: ""}")
            Button(onClick = {
                UserPreferences.clearAuth()
                SessionManager.notifyLoggedOut()
            }) { Text("Cerrar sesión") }
        }
    }
}
