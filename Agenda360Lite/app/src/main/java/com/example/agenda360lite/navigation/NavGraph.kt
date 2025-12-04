package com.example.agenda360lite.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import androidx.compose.runtime.LaunchedEffect
import com.example.agenda360lite.core.datastorage.SessionManager
import com.example.agenda360lite.auth.ui.LoginScreen
import com.example.agenda360lite.auth.ui.LoginViewModel
import com.example.agenda360lite.appointments.ui.DashboardScreen
import com.example.agenda360lite.appointments.ui.DashboardViewModel
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import com.example.agenda360lite.ui.components.BottomNavBar

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val CLIENTS = "clients"
    const val SERVICES = "services"
    const val APPOINTMENTS = "appointments"
    const val APPOINTMENT_DETAIL = "appointmentDetail/{id}"
    const val APPOINTMENT_FORM = "appointmentForm"
    const val PROFILE = "profile"
    const val CLIENT_CREATE = "clientCreate"
    const val SERVICE_CREATE = "serviceCreate"
    const val CLIENT_EDIT = "clientEdit/{id}"
    const val SERVICE_EDIT = "serviceEdit/{id}"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    LaunchedEffect(Unit) {
        SessionManager.loggedOut.collect { loggedOut ->
            if (loggedOut) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
                SessionManager.reset()
            }
        }
    }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    Scaffold(bottomBar = {
        if (currentRoute != Routes.LOGIN && currentRoute != Routes.REGISTER) {
            BottomNavBar(navController)
        }
    }) { padding ->
    NavHost(navController, startDestination = Routes.LOGIN, modifier = androidx.compose.ui.Modifier.padding(padding)) {
        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel()
            LoginScreen(viewModel = vm, onSuccess = {
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }, onRegister = {
                navController.navigate(Routes.REGISTER)
            })
        }
        composable(Routes.REGISTER) {
            com.example.agenda360lite.auth.ui.RegisterScreen(navController)
        }
        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel()
            DashboardScreen(vm, navController)
        }
        composable(Routes.CLIENTS) {
            val vm: com.example.agenda360lite.clients.ui.ClientListViewModel = viewModel()
            com.example.agenda360lite.clients.ui.ClientListScreen(vm, navController)
        }
        composable(Routes.SERVICES) {
            val vm: com.example.agenda360lite.services.ui.ServiceListViewModel = viewModel()
            com.example.agenda360lite.services.ui.ServiceListScreen(vm, navController)
        }
        composable(Routes.CLIENT_CREATE) {
            com.example.agenda360lite.clients.ui.ClientCreateScreen(navController)
        }
        composable(Routes.SERVICE_CREATE) {
            com.example.agenda360lite.services.ui.ServiceCreateScreen(navController)
        }
        composable(Routes.CLIENT_EDIT, arguments = listOf(navArgument("id") { type = NavType.LongType })) {
            val id = it.arguments?.getLong("id") ?: 0L
            com.example.agenda360lite.clients.ui.ClientEditScreen(id, navController)
        }
        composable(Routes.SERVICE_EDIT, arguments = listOf(navArgument("id") { type = NavType.LongType })) {
            val id = it.arguments?.getLong("id") ?: 0L
            com.example.agenda360lite.services.ui.ServiceEditScreen(id, navController)
        }
        composable(Routes.APPOINTMENT_FORM) {
            val vm: com.example.agenda360lite.appointments.ui.AppointmentFormViewModel = viewModel()
            com.example.agenda360lite.appointments.ui.AppointmentFormScreen(vm, navController)
        }
        composable(
            route = Routes.APPOINTMENT_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getLong("id") ?: -1L
            val vm: com.example.agenda360lite.appointments.ui.AppointmentDetailViewModel = viewModel()
            com.example.agenda360lite.appointments.ui.AppointmentDetailScreen(vm, idArg, navController)
        }
        composable(Routes.APPOINTMENTS) { Text("Appointments") }
        composable(Routes.PROFILE) { com.example.agenda360lite.profile.ProfileScreen(navController) }
    }
    }
}
