## Objetivo
- Corregir que el botón “Atrás” quede accesible y visible en pantallas de detalle y formulario usando TopAppBar con navegación.
- Preparar la continuación (DatePicker + Snackbars, paginación/meta) tras la corrección.

## Cambios UI (sin alterar lógica)
- En `AppointmentDetailScreen` y `AppointmentFormScreen`:
  - Envolver contenido en `Scaffold`.
  - Añadir `TopAppBar(title, navigationIcon)` con `navController.popBackStack()`.
  - Usar `contentPadding` del `Scaffold` para que el contenido no quede bajo la barra de estado.
- En `DashboardScreen` mantener fila de acciones desplazable, sin cambios de lógica.

## Archivos a actualizar
- `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/AppointmentDetailScreen.kt`
- `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/AppointmentFormScreen.kt`

## Criterios de Aceptación
- Al tocar una cita, la pantalla de detalle muestra una barra superior con flecha “Atrás” visible y funcional.
- La pantalla de formulario muestra barra superior con “Atrás”; contenido no queda oculto bajo el status bar.
- Compila y navega sin pantallas en blanco.

## Verificación
- Emulador: Login → Dashboard → tocar cita → detalle con TopAppBar y flecha que regresa.
- Emulador: “Nueva cita” → formulario con TopAppBar y flecha que regresa.

## Continuación (posterior a corrección)
- DatePicker y Snackbars en `AppointmentFormScreen`.
- Paginación con `meta.total` y control Anterior/Siguiente.
- Geolocalización y cámara (permisos + UI).
- Manejo global de 401 (logout).