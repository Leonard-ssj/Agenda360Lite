---
title: Plan de Ejecución – Pendientes Agenda360 Lite
---

# Plan de Ejecución – Pendientes Agenda360 Lite

## Estado actual (resumen)
- Backend: Ktor con JWT, MySQL (Exposed), CRUD de clientes/servicios/citas, disponibilidad y cambio de estado.
- Android: Login → Dashboard; listas con búsqueda/paginación básica; formulario de cita con disponibilidad y creación; navegación tras crear en ajuste fino.
- Documentación y Postman: actualizados (Quickstart, endpoints extendidos y pruebas).

## Objetivo de esta etapa
- Cerrar el flujo completo de citas en la app (lista, detalle, creación, cambio de estado), consolidar paginación con meta.total y manejo global de 401, con foco en pulido de UI/UX.

## Alcance
- Android: pulido de UI/UX, paginación visible, detalle de cita con cambio de estado y manejo 401.
- Backend: homogeneizar códigos y mensajes de error; mantener contrato estable (PUT updateStatus, availability, envelope).
- Docs/Entrega: actualización de guía y README con capturas; verificación Postman; APK debug.

## Entregables
- Paginación con `meta.total` y controles de “Anterior/Siguiente” basados en total.
- Manejo global de 401: logout y navegación a `login` con back stack limpio.
- Pulido de UI: paddings, divisores, estados vacíos y Snackbars consistentes.
- Detalle de cita: cambio de estado (DONE/CANCELLED) con feedback y reflejo en dashboard.
- Inicio de geolocalización y cámara: permisos, proveedor, captura y miniatura (persistencia local inicial).
- Documentación actualizada y APK debug.

## Plan de implementación inmediata (sin cronograma)
- Paginación y meta (Android):
  - Repos/VMs de clientes/servicios leen `Paged { items, meta { page, size, total } }`.
  - UI muestra “Página N de M” y deshabilita “Siguiente” cuando `(page+1)*size >= total`.
- Manejo 401 central (Android):
  - Coordinador de sesión: ante `clearAuth` (401) navegar a `login` y limpiar back stack.
- Pulido UI/UX:
  - Paddings consistentes, `HorizontalDivider`, Snackbars en éxito/error, estados vacíos con “Recargar”.
  - Botones de fecha en fila (formulario de cita).
- Mensajes homogéneos:
  - Backend: uniformar textos y códigos (400/404/409).
  - App: mapear envelope de error a Snackbars/estados.
- Cierre y verificación:
  - Actualizar docs con capturas; validar Postman; generar APK debug; pruebas en emulador.

## Orden de ejecución (prioridad)
1. Paginación visible con `meta.total` en Clientes/Servicios
   - Consumir `meta.total` en repos/VMs.
   - UI: mostrar “Página N de M” y desactivar “Siguiente” si `(page+1)*size >= total`.
   - Validación: listas navegan correctamente y coinciden con Postman.
2. Manejo global de 401
   - Al 401 o token borrado, limpiar sesión y navegar a `login` con back stack limpio.
   - Validación: expirar token y comprobar redirección automática.
3. Pulido UI del formulario y dashboard
   - Un único `LazyColumn`; DatePicker funcional; Snackbars coherentes; estados vacíos.
   - Botones de fecha en fila; paddings y divisores consistentes.
   - Validación: crear cita y retorno al dashboard con feedback correcto.
4. Detalle de cita con cambio de estado
   - `PUT status` desde detalle; mostrar Snackbar.
   - Al volver, dashboard refleja el nuevo estado.
   - Validación: cambio DONE/CANCELLED y refresco visible.
5. Lista de citas (si falta)
   - Pantalla de lista con filtros básicos (por fecha) y navegación a detalle.
   - Validación: navegación y apertura del detalle desde la lista.
6. Documentación y APK
   - Actualizar docs y capturas; verificar Postman; generar APK debug.

## Lo que falta implementar (resumen)
- Paginación visible con `meta.total` y control de navegación.
- Manejo centralizado de 401 con redirección a `login`.
- Pulido UI/UX: Snackbars, estados vacíos, paddings y divisores.
- Detalle de cita: cambio de estado con refresco de dashboard.
- Lista de citas: pantalla y navegación (si no está completa).
- Documentación y APK debug para entrega.

## ¿Estamos usando Jetpack?
- Sí. En la app se usa Jetpack Compose (UI Material3), Navigation Compose, DataStore (preferencias/JWT) y Hilt (DI). Room está planificado para cache local.

## Navegación final (definición)
- Rutas:
  - `login`
  - `dashboard`
  - `appointments` (lista)
  - `appointmentDetail/{id}`
  - `appointmentForm`
  - `clients` (lista)
  - `services` (lista)
  - `profile`
- Flujo:
  - Login → Dashboard (popUpTo login, inclusive=true).
  - Dashboard → acciones: Nueva cita, Ver clientes, Ver servicios, Ver lista de citas.
  - Lista de citas → Detalle de cita (cambiar estado) → volver a Dashboard.
  - Formulario de cita → seleccionar cliente/servicio/fecha/slot → Crear → volver a Dashboard.
  - Clientes/Servicios → búsqueda/paginación; volver a Dashboard.
  - Profile → ver datos y “Cerrar sesión”.
- Global:
  - 401/expiración → logout automático y navegación a `login` con back stack limpio.

## Detalle de implementación por área
- Android UI/UX
  - AppointmentFormScreen: Snackbars coherentes; redirección fiable tras crear; estados vacíos.
  - ClientListScreen/ServiceListScreen: `meta.total`, “Página N de M” y desactivar navegación sin más páginas.
  - AppointmentDetailScreen: botones DONE/CANCELLED con Snackbars; refrescar dashboard al volver.
  - Manejo 401: al limpiar token, navegar a `login` (limpiar back stack); mensajes claros.
- Backend
  - Mantener contrato actual: `PUT /appointments/{id}/status`, `GET /appointments/availability`, envelope estándar.
  - Revisar errores uniformes: BAD_REQUEST, NOT_FOUND, CONFLICT.

## Pruebas y criterios de aceptación
- Backend (Postman)
  - Register 201; Login 200 (capturar token); Availability 200/400; Create 201/409; Update Status 200/404.
- Android (emulador)
  - Login y navegación a dashboard.
  - Listas: búsqueda/paginación con “Página N de M” y desactivación correcta.
  - Nueva cita: seleccionar cliente/servicio/fecha/slot; crear con Snackbar y volver al dashboard.
  - Detalle: cambiar estado y ver reflejo en dashboard.

## Riesgos y mitigaciones
- Bloqueo `R.jar` (Windows): `gradlew --stop`, `clean assembleDebug`.
- 401/expiración: interceptor limpia sesión; coordinador redirige a `login`.
- Zonas horarias: normalizar ISO 8601 en app y backend (UTC).
- Datos vacíos: mensajes claros y acción de “Recargar”.

## Referencias de código
- Backend
  - Rutas citas: `backend-ktor/src/main/kotlin/routes/AppointmentRoutes.kt`
  - Repos citas: `backend-ktor/src/main/kotlin/repository/DbAppointmentRepository.kt`
  - Seguridad JWT: `backend-ktor/src/main/kotlin/Security.kt`
- Android
  - NavGraph: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/navigation/NavGraph.kt`
  - Form cita: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/AppointmentFormScreen.kt`
  - Detalle cita: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/AppointmentDetailScreen.kt`
  - Listas: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/clients/ui/ClientListScreen.kt`, `.../services/ui/ServiceListScreen.kt`
  - APIs: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/data/remote/AppointmentApi.kt`
