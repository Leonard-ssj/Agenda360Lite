## Diagnóstico Inicial
- Revisar rutas en `NavGraph` y nombres exactos usados al navegar ("dashboard" vs constantes).
- Confirmar `NavHost` con `startDestination` correcto y `composable("dashboard")` existente.
- Verificar inyección de `ViewModel` en pantallas: usar `@HiltViewModel` + `hiltViewModel()`.
- Revisar `LoginViewModel` → navegación sólo una vez (evitar bucles); usar `LaunchedEffect` para eventos.
- Comprobar que `DashboardViewModel` no bloquea UI: `viewModelScope.launch(Dispatchers.IO)` y estados claros (loading/error/data).
- Inspeccionar Logcat tras login para NPE/IllegalState/Navigation exceptions.

## Implementación (Android)
- `MainActivity.kt`: `@AndroidEntryPoint`, `rememberNavController()`, `NavHost` con rutas definidas.
- `NavGraph.kt`:
  - Asegurar `startDestination="login"` y `composable("dashboard")`.
  - Navegación desde login: `navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }`.
  - Inyectar ViewModels con `hiltViewModel()`.
- `LoginViewModel`/`LoginScreen`:
  - Exponer `UiState` (loading/success/error) y lanzar navegación con `LaunchedEffect(state.isLoggedIn)`.
  - Manejar errores visibles (Snackbar/Toast) y evitar pantalla en blanco.
- `DashboardViewModel`/`DashboardScreen`:
  - `@HiltViewModel` con repos inyectados.
  - `init { loadTodayAppointments() }` en `viewModelScope.launch(Dispatchers.IO)`.
  - UI con estados: mostrar loading, lista vacía o error; siempre renderizar contenido.
- `AuthRepository`/`JwtInterceptor`:
  - Confirmar token en DataStore y Authorization en llamadas; manejar 401 (logout) como mejora futura.

## Pruebas (End‑to‑End)
- Android:
  - Login → `LaunchedEffect` navega a dashboard: UI no en blanco; ver loading → datos.
  - Simular error backend (desconectar) → ver estado de error en UI.
  - CRUD rápido: crear cliente/servicio/cita y ver elementos en dashboard/lista.
- Backend + Postman:
  - Mantener pruebas de login, paginación y citas; validar 409 en conflicto y `availability`.

## Documentación
- Actualizar `docs/Analisis_Proyecto_A360Lite.md` y `docs/Fases_Plan_A360Lite.md` con:
  - Flujo de navegación tras login.
  - Estados de UI y manejo de errores.
  - Pasos para pruebas Android + backend.

## Criterios de Aceptación
- Tras login, navegación a dashboard sin pantalla en blanco.
- Dashboard muestra loading y luego datos o mensaje vacío; no hay bloqueos.
- ViewModels inyectados con Hilt y llamadas en IO.
- Pruebas Postman siguen pasando; app muestra conflictos 409 correctamente.