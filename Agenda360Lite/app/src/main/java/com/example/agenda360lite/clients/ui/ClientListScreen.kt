package com.example.agenda360lite.clients.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(vm: ClientListViewModel, navController: NavController? = null) {
    val state = vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.load() }
    Scaffold(topBar = { TopAppBar(title = { Text("Clientes") }, navigationIcon = { if (navController != null) IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            val query = remember { mutableStateOf("") }
            OutlinedTextField(value = query.value, onValueChange = {
                query.value = it
                vm.setQuery(it)
            }, label = { Text("Buscar") })
            Button(onClick = { vm.page = 0; vm.load() }) { Text("Buscar") }
            Button(onClick = { navController?.navigate(com.example.agenda360lite.navigation.Routes.CLIENT_CREATE) }) { Text("Nuevo cliente") }
            HorizontalDivider()
            when (val s = state.value) {
                is ClientListState.Loading -> Text("Cargando...")
                is ClientListState.Error -> Text("Error: ${s.message}")
                is ClientListState.Success -> {
                    if (s.items.isEmpty()) Text("Sin clientes")
                    else LazyColumn { items(s.items) { c ->
                        com.example.agenda360lite.ui.components.ListItemCard(
                            title = c.name,
                            subtitle = "#${c.id} • ${c.phone.orEmpty()} ${c.email?.let { "• $it" } ?: ""}",
                            leadingIcon = Icons.Filled.Person,
                            trailing = {
                                com.example.agenda360lite.ui.components.TonalButton(text = "Editar", onClick = { navController?.navigate("clientEdit/${c.id}") }, leadingIcon = Icons.Filled.Edit)
                            }
                        )
                    } }
                    val total = s.meta.total
                    val size = vm.size
                    val page = vm.page
                    val maxPageIndex = ((total + size - 1) / size).toInt() - 1
                    Button(onClick = { vm.prevPage(); vm.load() }, enabled = page > 0) { Text("Anterior") }
                    Button(onClick = { vm.nextPage(); vm.load() }, enabled = if (total == 0L) false else page < maxPageIndex) { Text("Siguiente") }
                    Text("Página ${page + 1} de ${if (total == 0L) 1 else ((total + size - 1) / size)} • Total ${total}")
                }
            }
        }
    }
}
