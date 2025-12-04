package com.example.agenda360lite.appointments.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import com.example.agenda360lite.core.datastorage.UserPreferences
import com.example.agenda360lite.core.datastorage.SessionManager
import com.example.agenda360lite.ui.components.AvatarInitials
import com.example.agenda360lite.ui.components.PrimaryButton
import com.example.agenda360lite.ui.components.TonalButton
import com.example.agenda360lite.ui.components.SectionHeader
import com.example.agenda360lite.ui.components.EmptyState
import com.example.agenda360lite.ui.components.StatsCard
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agenda360lite.appointments.ui.components.AppointmentCard
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DashboardViewModel, navController: NavController? = null) {
    val state = vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.loadToday() }
    LaunchedEffect(navController) {
        navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("refreshDashboard")?.observeForever {
            if (it == true) {
                vm.loadToday()
                navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refreshDashboard")
            }
        }
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Agenda360 • Dashboard") }, actions = {
        Button(onClick = { navController?.navigate(com.example.agenda360lite.navigation.Routes.PROFILE) }) { Text("Perfil") }
        Button(onClick = {
            UserPreferences.clearAuth(); SessionManager.notifyLoggedOut()
        }) { Text("Cerrar sesión") }
    }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))).padding(16.dp)) {
                Row {
                    AvatarInitials(name = UserPreferences.userName)
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(" Hola, ${UserPreferences.userName ?: ""}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    PrimaryButton(text = "Actualizar", onClick = { vm.loadToday() }, leadingIcon = Icons.Filled.Refresh)
                    val prev = LocalDate.now(ZoneOffset.UTC).minusDays(1).toString()
                    TonalButton(text = "Ver ayer", onClick = { vm.loadDate(prev) }, leadingIcon = Icons.Filled.Refresh)
                    if (navController != null) {
                        PrimaryButton(text = "Nueva cita", onClick = { navController.navigate(com.example.agenda360lite.navigation.Routes.APPOINTMENT_FORM) }, leadingIcon = Icons.Filled.Add)
                        TonalButton(text = "Clientes", onClick = { navController.navigate(com.example.agenda360lite.navigation.Routes.CLIENTS) }, leadingIcon = Icons.Filled.Group)
                        TonalButton(text = "Servicios", onClick = { navController.navigate(com.example.agenda360lite.navigation.Routes.SERVICES) }, leadingIcon = Icons.Filled.Person)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SectionHeader(text = "Citas de hoy")
                when (val s = state.value) {
                    is DashboardState.Success -> {
                        val scheduled = s.items.count { it.status == "SCHEDULED" }
                        val done = s.items.count { it.status == "DONE" }
                        val cancelled = s.items.count { it.status == "CANCELLED" }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatsCard(title = "Programadas", value = scheduled)
                            Spacer(modifier = Modifier.size(8.dp))
                            StatsCard(title = "Completadas", value = done)
                            Spacer(modifier = Modifier.size(8.dp))
                            StatsCard(title = "Canceladas", value = cancelled)
                        }
                    }
                    else -> {}
                }
            }
            when (val s = state.value) {
                is DashboardState.Loading -> Text("Cargando citas de hoy...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                is DashboardState.Error -> Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                is DashboardState.Success -> {
                    if (s.items.isEmpty()) {
                        EmptyState(message = "No hay citas para hoy", ctaText = "Nueva cita", onCta = { navController?.navigate(com.example.agenda360lite.navigation.Routes.APPOINTMENT_FORM) })
                    }
                    LazyColumn {
                        items(s.items) { appt ->
                            AppointmentCard(
                                id = appt.id,
                                clientName = "Cliente #${appt.clientId}",
                                serviceName = "Servicio #${appt.serviceId}",
                                dateTime = appt.dateTime,
                                status = appt.status,
                                onClick = { navController?.navigate("appointmentDetail/${appt.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}
