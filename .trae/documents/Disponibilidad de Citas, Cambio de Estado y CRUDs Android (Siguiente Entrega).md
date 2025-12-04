## Backend
- Validaciones DTOs: email, price≥0, duration≥1, fecha válida; respuestas uniformes 400/404/409.
- Endpoint disponibilidad: GET /api/v1/appointments/availability?date=YYYY-MM-DD&serviceId=ID
  - Jornada configurable (.env, ej. 09:00–18:00), slots libres según durationMinutes.
- Endpoint estado: PUT /api/v1/appointments/{id}/status { status, reason? }
  - Transiciones válidas: SCHEDULED→DONE/CANCELLED; 409 si inválida.
- Postman: añadir requests de availability y estado con tests.

## Android
- Clientes/Servicios: listas con búsqueda/paginación, alta/edición, detalle.
- Cita nueva: formulario con selector fecha/hora que consulta disponibilidad del backend.
- Detalle de cita: botón para cambiar estado (DONE/CANCELLED) y feedback.
- UX/Estados: loading, vacío, error consistente; manejar 401 posteriormente.

## Documentación
- Actualizar docs (Analisis/Fases) con nuevos endpoints, flujos y criterios.

## Criterios de Aceptación
- Availability devuelve slots libres correctos.
- Cambio de estado responde 200 y refleja en listados.
- CRUDs Android funcionales contra backend; formulario de cita usa availability.
- Postman y docs actualizados con pruebas y ejemplos.