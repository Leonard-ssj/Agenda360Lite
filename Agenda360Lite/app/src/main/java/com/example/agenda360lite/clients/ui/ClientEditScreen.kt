package com.example.agenda360lite.clients.ui

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
import com.example.agenda360lite.clients.data.repository.ClientRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientEditScreen(id: Long, navController: NavController? = null) {
    val repo = ClientRepository()
    val name = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(id) {
        repo.getById(id)?.let {
            name.value = it.name
            phone.value = it.phone.orEmpty()
            email.value = it.email.orEmpty()
            notes.value = it.notes.orEmpty()
        }
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Editar cliente #$id") }, navigationIcon = { Button(onClick = { navController?.popBackStack() }) { Text("Atrás") } }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes.value, onValueChange = { notes.value = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                scope.launch {
                    if (name.value.isBlank()) { snackbar.showSnackbar("Nombre requerido"); return@launch }
                    val updated = repo.update(id, name.value.trim(), phone.value.trim().ifBlank { null }, email.value.trim().ifBlank { null }, notes.value.trim().ifBlank { null })
                    if (updated != null) { snackbar.showSnackbar("Cliente actualizado"); navController?.popBackStack() } else snackbar.showSnackbar("No se pudo actualizar")
                }
            }) { Text("Guardar cambios") }
        }
    }
}

