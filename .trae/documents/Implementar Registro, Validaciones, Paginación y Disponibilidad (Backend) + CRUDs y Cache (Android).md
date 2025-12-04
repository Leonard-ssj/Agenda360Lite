## Backend: Autenticación y Registro

* Crear `POST /api/v1/auth/register` con validaciones: email único, formato de email, password mínima.

* Hashear contraseñas con `BCrypt` y persistir en `users` (rol por defecto `USER`).

* Ajustar `AuthService` para normalizar errores: `INVALID_CREDENTIALS`, `EMAIL_IN_USE`.

* Añadir pruebas Postman para registro y login.

## Backend: Validaciones y Errores Consistentes

* Validar DTOs (clientes, servicios, citas): campos requeridos, rango de valores y formatos.

* Unificar respuestas con `ResponseEnvelope` y códigos HTTP: 400/401/404/409.

* Asegurar que `PUT/DELETE` distingan correctamente 404 (no existe) y 409 (conflicto citas).

## Backend: Paginación y Búsqueda con Metadatos

* Extender `GET /api/v1/clients` y `/services` para devolver `meta: { page, size, total }` y `data`.

* Implementar conteo total con condiciones de búsqueda (`q`) y límites `page/size`.

* Mantener ordenamiento estable (por `updatedAt` desc o `id` desc).

## Backend: Citas – Disponibilidad y Estados

* Crear `GET /api/v1/appointments/availability?date=YYYY-MM-DD` que devuelve slots disponibles considerando `durationMinutes`.

* Implementar transiciones de estado válidas: `SCHEDULED → DONE/CANCELLED` con motivo opcional.

* Documentar y probar conflictos (409) en creación/edición.

## Postman: Collection y Entorno

* Añadir requests de registro, paginación con metadatos y disponibilidad.

* Incorporar tests que capturen `token` y verifiquen códigos esperados (200/201/400/409).

## Android: CRUDs y UI

* Clientes: lista (búsqueda/paginación), crear/editar/eliminar y detalle.

* Servicios: lista (búsqueda/paginación), crear/editar/eliminar y detalle.

* Citas: crear/editar con selector fecha/hora; mostrar errores de conflicto (409) en UI.

* Manejo global de 401: interceptor que fuerza logout y limpieza de sesión.

## Documentación

* Actualizar `docs/` con: arranque backend/app, `.env`, endpoints y ejemplos.

* Incluir flujos de pruebas (Postman/Android), criterios de aceptación y casos de error.

## Criterios de Aceptación

* Registro crea usuario y login funciona con contraseña hasheada.

* Paginación retorna `meta.total` correcto y `data` filtrada por `q`.

* Disponibilidad devuelve slots y creación respeta conflictos.

* App Android realiza CRUDs y maneja 401/409 con mensajes claros.

