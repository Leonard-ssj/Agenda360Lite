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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenda360lite.services.data.repository.ServiceRepository
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceEditScreen(id: Long, navController: NavController? = null) {
    val repo = ServiceRepository()
    val name = remember { mutableStateOf("") }
    val duration = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val desc = remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(id) {
        repo.getById(id)?.let {
            name.value = it.name
            duration.value = it.durationMinutes.toString()
            price.value = it.price.toString()
            desc.value = it.description.orEmpty()
        }
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Editar servicio #$id") }, navigationIcon = { Button(onClick = { navController?.popBackStack() }) { Text("Atrás") } }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = duration.value, onValueChange = { duration.value = it }, label = { Text("Duración (min)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price.value, onValueChange = { price.value = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc.value, onValueChange = { desc.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                scope.launch {
                    val dur = duration.value.toIntOrNull() ?: -1
                    val pr = price.value.toDoubleOrNull() ?: -1.0
                    if (name.value.isBlank() || dur <= 0 || pr < 0.0) { snackbar.showSnackbar("Datos inválidos"); return@launch }
                    val updated = repo.update(id, name.value.trim(), dur, pr, desc.value.trim().ifBlank { null })
                    if (updated != null) { snackbar.showSnackbar("Servicio actualizado"); navController?.popBackStack() } else snackbar.showSnackbar("No se pudo actualizar")
                }
            }) { Text("Guardar cambios") }
        }
    }
}

