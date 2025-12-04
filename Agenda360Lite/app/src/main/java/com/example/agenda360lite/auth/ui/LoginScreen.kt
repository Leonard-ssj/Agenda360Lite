package com.example.agenda360lite.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.agenda360lite.ui.components.PrimaryButton
import com.example.agenda360lite.ui.components.HeroHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel, onSuccess: () -> Unit, onRegister: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPwd = remember { mutableStateOf(false) }
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(topBar = { TopAppBar(title = { Text("Agenda360 • Iniciar sesión") }) }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)))) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1556761175-4b46a572b786?w=1200",
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ElevatedCard(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bienvenido", style = MaterialTheme.typography.titleLarge)
                    Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        trailingIcon = { Icon(if (showPwd.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null) },
                        visualTransformation = if (showPwd.value) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(text = "Login", onClick = { viewModel.onLoginClick(email.value.trim(), password.value, onSuccess) }, modifier = Modifier.fillMaxWidth())
                    TextButton(onClick = { onRegister() }) { Text("¿No tienes cuenta? Crear cuenta", color = MaterialTheme.colorScheme.primary) }
                    HorizontalDivider()
                    when (val s = state.value) {
                        is UiState.Error -> { LaunchedEffect(s.message) { snackbarHostState.showSnackbar(s.message) } }
                        is UiState.Loading -> { Text("Cargando...", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        else -> {}
                    }
                }
            }
        }
    }
}
