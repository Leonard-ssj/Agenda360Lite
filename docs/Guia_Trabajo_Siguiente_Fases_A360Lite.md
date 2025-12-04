---
title: Guía de Trabajo – Agenda360 Lite
---

# Guía de Trabajo – Agenda360 Lite

## Estado Actual

- Backend Ktor: JWT, MySQL (Exposed), CRUD de clientes/servicios/citas, disponibilidad y cambio de estado.
- Android: Login → Dashboard; listas de clientes/servicios con búsqueda/paginación; formulario de cita básico (consulta disponibilidad y crea).
- Documentación y Postman: actualizados con Quickstart y endpoints extendidos.

## Fase en Curso (Fase 5)

- Objetivo: cerrar CRUD móvil completo y flujo de citas (lista, detalle, creación con disponibilidad, cambio de estado).
- Entregables:
  - Pantalla y ViewModel de detalle de cita con cambio de estado.
  - Formulario de cita con selector de fecha (DatePicker) y feedback (Snackbars).
  - Paginación con metadatos visibles (`meta.total`) y desactivación de navegación si no hay más páginas.

## Próxima Iteración (Checklist)

- Backend
  - Alinear contrato con cliente Android para `updateStatus` (usar `PUT`).
  - Revisar mensajes y códigos de error uniformes (400/404/409).
- Android
  - `AppointmentDetailScreen` y `AppointmentDetailViewModel`: visualizar datos de cita y botón de estado (DONE/CANCELLED).
  - `AppointmentFormScreen`: añadir DatePicker, validar selección cliente/servicio/slot y mostrar Snackbars.
  - Listas: mostrar `meta.total`, controlar `page/size`, desactivar “Siguiente” sin más ítems.
  - Manejo de 401 global: logout y limpieza de sesión.
- Documentación
  - Añadir capturas y ejemplos de flujos; consolidar criterios de aceptación.

## Pruebas y Criterios de Aceptación

- Postman (entorno “Agenda360Lite Local”):
  - Register 201, Login 200 (captura `{{token}}`).
  - Clients/Services: Create/List con `meta.total` y filtros `q/page/size`.
  - Appointments: Create, List por fecha/rango, Availability 200, Conflict 201/409, Update Status 200.
- Android:
  - Login con usuario de BD; `JwtInterceptor` agrega `Authorization`.
  - Dashboard: acciones visibles; lista de citas del día.
  - Nueva cita: seleccionar cliente, servicio, fecha y slot; crear y volver a dashboard.
  - Detalle cita: cambiar estado y ver reflejo en dashboard.

## Troubleshooting Común

- Bloqueo `R.jar` al compilar (Windows):
  - Parar Daemon (`gradlew --stop`), `clean assembleDebug`.
- 401 tras login: confirmar token en DataStore y `BASE_URL = http://10.0.2.2:8080/` en emulador.
- Availability 500: verificar `serviceId` válido; backend devuelve 400 `SERVICE_NOT_FOUND` si no existe.
- Conflictos de horario: crear con slot de disponibilidad para evitar 409.

## Convenciones y Rutas

- Base Backend: `http://localhost:8080/` (app usa `http://10.0.2.2:8080/`).
- Endpoints clave:
  - `POST /api/v1/auth/login`, `POST /api/v1/auth/register`
  - `GET/POST/PUT/DELETE /api/v1/clients`
  - `GET/POST/PUT/DELETE /api/v1/services`
  - `GET/POST/PUT/DELETE /api/v1/appointments`
  - `GET /api/v1/appointments/availability?date=&serviceId=`
  - `PUT /api/v1/appointments/{id}/status`

## Plan de Ejecución (Sugerencia)

- Día 1–2
  - Implementar `AppointmentDetailScreen/VM` y `PUT updateStatus` en cliente.
  - Añadir DatePicker y Snackbars en `AppointmentFormScreen`.
- Día 3–4
  - Paginación con metadatos visibles y control de navegación.
  - Manejo global de 401 (logout limpio).
- Día 5–6 (Fase 6)
  - Geolocalización: permisos, proveedor, integración en formulario.
  - Cámara: captura y miniatura; opcional subida.
- Día 7 (Fase 7)
  - Pulido de UI/UX y documentación final; APK y pruebas end‑to‑end.

## Referencias (Código)

- Backend
  - Rutas citas: `backend-ktor/src/main/kotlin/routes/AppointmentRoutes.kt`
  - Repos citas (conflicto/disponibilidad/estado): `backend-ktor/src/main/kotlin/repository/DbAppointmentRepository.kt`
  - Seguridad JWT: `backend-ktor/src/main/kotlin/Security.kt`
- Android
  - NavGraph: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/navigation/NavGraph.kt`
  - Dashboard: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/DashboardScreen.kt`
  - Form cita: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/ui/AppointmentFormScreen.kt`
  - APIs:
    - Citas: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/appointments/data/remote/AppointmentApi.kt`
    - Clientes: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/clients/data/remote/ClientApi.kt`
    - Servicios: `Agenda360Lite/app/src/main/java/com/example/agenda360lite/services/data/remote/ServiceApi.kt`

---

# Guía de Trabajo Pendiente y Cómo Probar

## Qué Falta (Checklist accionable)

- Completar flujo de citas en Android:
  - Lista y detalle de citas (pantallas y ViewModels).
  - Cambio de estado desde detalle (DONE/CANCELLED) con feedback (Snackbar).
- Mejorar formulario de cita:
  - Selector de fecha (DatePicker) y validación cliente/servicio/slot.
  - Feedback visual (success/error), y refresco del dashboard.
- Búsqueda y paginación:
  - Mostrar `meta.total` en listas de clientes/servicios.
  - Desactivar “Siguiente” cuando no haya más páginas.
- Alinear método `updateStatus` en Android a `PUT` (contrato real del backend).
- Geolocalización y cámara (Fase 6):
  - Permisos y proveedor de ubicación; botón “Usar mi ubicación actual”.
  - Captura de foto (CameraX/Intent) y miniatura; asociar URI.
- Manejo de 401 global:
  - Logout y limpieza de sesión automática.
- Pulido y entrega (Fase 7):
  - UI/UX (espaciados, tipografía, estados vacíos, snackbars).
  - README final y APK; capturas; colección Postman con todos los casos.

## Cómo Probar – Backend

- Preparación:
  - `.env` con DB y JWT; opcional `WORK_START/WORK_END` para disponibilidad.
  - Arranque: `cd backend-ktor && .\\gradlew.bat run` → `http://localhost:8080/`.
- Autenticación:
  - `POST /api/v1/auth/register` → 201.
  - `POST /api/v1/auth/login` → 200 y capturar token.
- Clientes y servicios:
  - `POST /api/v1/clients` y `POST /api/v1/services` → 201.
  - `GET /api/v1/clients?page=0&size=10&q=...` y `GET /api/v1/services?page=0&size=10&q=...` → validar `data.items` y `data.meta.total`.
- Citas:
  - `GET /api/v1/appointments?date=YYYY-MM-DD` → citas del día.
  - `GET /api/v1/appointments/availability?date=&serviceId=` → 200 slots o 400 si service no existe.
  - `POST /api/v1/appointments` → 201; repetir con solape para 409.
  - `PUT /api/v1/appointments/{id}/status` `{status:"DONE"}` → 200 o 404.
- Errores y recuperación:
  - Quitar `serviceId` en availability → 400 BAD_REQUEST.
  - Cambiar estado con id inexistente → 404.

## Cómo Probar – Android (Emulador)

- Preparación:
  - BaseUrl app: `http://10.0.2.2:8080/`; `INTERNET` y `usesCleartextTraffic=true` en manifest.
  - Abrir backend antes del login.
- Login y navegación:
  - Login con usuario de BD → redirección a dashboard.
  - En dashboard: “Actualizar”, “Ver ayer”, “Nueva cita”, “Ver clientes”, “Ver servicios”.
- Listas y paginación:
  - “Ver clientes/servicios” → probar búsqueda por texto y navegar Anterior/Siguiente; mostrar `meta.total` (pendiente).
- Formulario de cita:
  - Seleccionar cliente/servicio, ajustar fecha (anterior/siguiente), ver slots y crear; confirmar retorno a dashboard.
  - Probar crear con solape para ver manejo de error (Snackbar, pendiente).
- Detalle de cita (pendiente):
  - Abrir cita y cambiar estado; refrescar dashboard.
- Geo y cámara (pendiente de implementar):
  - Al crear cita, capturar lat/lon y foto; ver miniatura y lat/lon en detalle.

## Criterios de Aceptación (Resumen)

- App móvil: login → CRUDs clientes/servicios → crear y gestionar citas (availability, estado) → flujo estable con feedback.
- Backend: endpoints protegidos responden con códigos correctos y `ResponseEnvelope` estándar.
- Geo/cámara: integrados en el flujo de citas con persistencia y visualización.
- Entrega: README/Docs actualizados, colección Postman completa, APK funcional.

## Riesgos y Mitigaciones

- Gradle/R.jar lock: `gradlew --stop`, luego `clean assembleDebug`.
- 401 expira token: agregar logout global en interceptor.
- UTC y fechas: normalizar ISO 8601 en app y backend.
- Datos vacíos (sin clientes/servicios): mensajes claros y “Recargar” en formulario.

## Próxima Sesión (SOLO Coder)

- Implementar `AppointmentDetailScreen/VM` y `PUT updateStatus` en cliente.
- Añadir DatePicker y Snackbars en `AppointmentFormScreen`.
- Mostrar `meta.total` y controlar paginación en listas.
