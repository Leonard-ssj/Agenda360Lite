package com.example.agenda360lite.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agenda360lite.navigation.Routes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person

data class BottomItem(val route: String, val title: String, val icon: ImageVector)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Home),
        BottomItem(Routes.CLIENTS, "Clientes", Icons.Filled.Group),
        BottomItem(Routes.SERVICES, "Servicios", Icons.Filled.Build),
        BottomItem(Routes.PROFILE, "Perfil", Icons.Filled.Person),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.DASHBOARD) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) }
            )
        }
    }
}

