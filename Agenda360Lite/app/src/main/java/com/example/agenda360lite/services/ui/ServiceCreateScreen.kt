package com.example.agenda360lite.services.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenda360lite.services.data.repository.ServiceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCreateScreen(navController: NavController? = null) {
    val name = remember { mutableStateOf("") }
    val duration = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val desc = remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val repo = ServiceRepository()
    val scope = rememberCoroutineScope()
    Scaffold(topBar = { TopAppBar(title = { Text("Nuevo servicio") }, navigationIcon = { Button(onClick = { navController?.popBackStack() }) { Text("Atrás") } }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = duration.value, onValueChange = { duration.value = it }, label = { Text("Duración (min)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price.value, onValueChange = { price.value = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc.value, onValueChange = { desc.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                scope.launch {
                    try {
                        val dur = duration.value.toIntOrNull() ?: -1
                        val pr = price.value.toDoubleOrNull() ?: -1.0
                        if (dur <= 0 || pr < 0.0 || name.value.isBlank()) {
                            snackbar.showSnackbar("Datos inválidos")
                        } else {
                            val created = repo.create(name.value.trim(), dur, pr, desc.value.trim().ifBlank { null })
                            if (created != null) {
                                snackbar.showSnackbar("Servicio creado")
                                name.value = ""; duration.value = ""; price.value = ""; desc.value = ""
                                navController?.popBackStack()
                            } else snackbar.showSnackbar("No se pudo crear")
                        }
                    } catch (e: Exception) {
                        snackbar.showSnackbar("Error: ${e.message}")
                    }
                }
            }, enabled = name.value.isNotBlank()) { Text("Guardar") }
        }
    }
}
