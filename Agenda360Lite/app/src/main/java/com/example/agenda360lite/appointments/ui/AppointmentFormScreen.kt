package com.example.agenda360lite.appointments.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(vm: AppointmentFormViewModel, navController: NavController? = null) {
    val st = vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.init() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Nueva cita") }, navigationIcon = { Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.padding(8.dp)) }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            item {
                val s = st.value
                androidx.compose.runtime.LaunchedEffect(s.error) {
                    if (s.error != null) snackbarHostState.showSnackbar(s.error)
                }
                androidx.compose.runtime.LaunchedEffect(s.success) {
                    if (s.success) {
                        scope.launch { snackbarHostState.showSnackbar("Cita creada") }
                        navController?.popBackStack()
                        vm.resetSuccess()
                    }
                }
                if (s.loading) Text("Cargando...")
                s.error?.let { Text("Error: $it") }
                HorizontalDivider()
            }
            item {
                val s = st.value
                val context = LocalContext.current
                Text("Fecha: ${s.selectedDate}")
                Row {
                Button(onClick = {
                    val d = java.time.LocalDate.parse(s.selectedDate).minusDays(1).toString()
                    vm.selectDate(d)
                }) { Icon(Icons.Filled.NavigateBefore, contentDescription = null); Text(" Día anterior") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val d = java.time.LocalDate.parse(s.selectedDate).plusDays(1).toString()
                    vm.selectDate(d)
                }) { Icon(Icons.Filled.NavigateNext, contentDescription = null); Text(" Día siguiente") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val parts = s.selectedDate.split("-")
                    val y = parts[0].toInt()
                    val m = parts[1].toInt() - 1
                    val d = parts[2].toInt()
                    android.app.DatePickerDialog(context, { _, yy, mm, dd ->
                        val mm1 = (mm + 1).toString().padStart(2, '0')
                        val dd1 = dd.toString().padStart(2, '0')
                        vm.selectDate("$yy-$mm1-$dd1")
                    }, y, m, d).show()
                }) { Icon(Icons.Filled.CalendarToday, contentDescription = null); Text(" Seleccionar fecha") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
            item { Text("Clientes") }
            val sClients = st.value
            if (sClients.clients.isEmpty()) {
                item { Text("Sin clientes. Cree alguno desde Servicios/Postman o recargue.") }
                item { Button(onClick = { vm.init() }) { Text("Recargar") } }
            } else {
                items(sClients.clients) { c ->
                    Button(onClick = { vm.selectClient(c.id) }, enabled = !sClients.loading) { Text("#${c.id} ${c.name}${if (sClients.selectedClientId == c.id) " (Seleccionado)" else ""}") }
                }
            }
            item { HorizontalDivider() }
            item { Text("Servicios") }
            val sServices = st.value
            if (sServices.services.isEmpty()) {
                item { Text("Sin servicios. Cree alguno en Servicios o Postman.") }
                item { Button(onClick = { vm.init() }) { Text("Recargar") } }
            } else {
                items(sServices.services) { sv ->
                    Button(onClick = { vm.selectService(sv.id) }, enabled = !sServices.slotsLoading) { Text("#${sv.id} ${sv.name}${if (sServices.selectedServiceId == sv.id) " (Seleccionado)" else ""}") }
                }
            }
            item { HorizontalDivider() }
            item { Text("Slots") }
            val sSlots = st.value
            if (sSlots.slotsLoading) {
                item { Text("Cargando slots...") }
            }
            if (sSlots.slots.isEmpty()) {
                item { Text("Seleccione servicio y fecha para ver disponibilidad.") }
            } else {
                items(sSlots.slots) { slot ->
                    Button(onClick = { vm.selectSlot(slot) }, enabled = !sSlots.slotsLoading) { Text("${slot}${if (sSlots.selectedSlot == slot) " (Seleccionado)" else ""}") }
                }
            }
            item { OutlinedTextField(value = st.value.selectedSlot ?: "", onValueChange = { }, label = { Text("Slot seleccionado") }) }
            item {
                val s = st.value
                Button(onClick = { vm.create() }, enabled = s.selectedClientId != null && s.selectedServiceId != null && s.selectedSlot != null && !s.loading && !s.slotsLoading) { Text("Crear") }
            }
        }
    }
}
