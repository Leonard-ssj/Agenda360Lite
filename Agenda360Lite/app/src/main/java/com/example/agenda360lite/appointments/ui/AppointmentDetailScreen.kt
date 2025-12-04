package com.example.agenda360lite.appointments.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(vm: AppointmentDetailViewModel, id: Long, navController: NavController? = null) {
    val state = vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(id) { vm.load(id) }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de cita") }, navigationIcon = { Button(onClick = { navController?.popBackStack() }) { Text("Atrás") } }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when (val s = state.value) {
                is AppointmentDetailState.Loading -> Text("Cargando...")
                is AppointmentDetailState.Error -> Text("Error: ${s.message}")
                is AppointmentDetailState.Success -> {
                    val a = s.item
                    Card(colors = CardDefaults.cardColors()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cita #${a.id}")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Cliente: #${a.clientId}")
                            Text("Servicio: #${a.serviceId}")
                            Text("Fecha: ${a.dateTime}")
                            Text("Estado: ${a.status}")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Button(onClick = {
                        vm.setStatus(a.id, "DONE")
                        scope.launch { snackbarHostState.showSnackbar("Marcado DONE") }
                        navController?.previousBackStackEntry?.savedStateHandle?.set("refreshDashboard", true)
                        navController?.popBackStack()
                    }) { Text("Marcar DONE") }
                    Button(onClick = {
                        vm.setStatus(a.id, "CANCELLED")
                        scope.launch { snackbarHostState.showSnackbar("Marcado CANCELLED") }
                        navController?.previousBackStackEntry?.savedStateHandle?.set("refreshDashboard", true)
                        navController?.popBackStack()
                    }) { Text("Marcar CANCELLED") }
                }
            }
        }
    }
}
