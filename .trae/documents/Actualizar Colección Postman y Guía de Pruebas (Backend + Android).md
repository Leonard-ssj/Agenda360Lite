## Cambios en la Colección Postman

* Auth:
  * Añadir "Register": POST `{{baseUrl}}/api/v1/auth/register` con body `{ name, email, password }` y test que verifique `201`.
  * Mantener "Login" y su test para capturar `{{token}}`.

* Clients/Services:
  * Mantener "Get by ID".
  * Ajustar "List" para paginación/búsqueda; agregar test que verifique presencia de `data.items` y `data.meta.total` dentro del `ResponseEnvelope`.

* Appointments:
  * Añadir "Availability": GET `{{baseUrl}}/api/v1/appointments/availability?date={{date}}&serviceId={{serviceId}}` con test `200`.
  * Mantener "List by date", "List by range", "Get by ID", "Create/Update/Delete".
  * Mantener "Create Appointment Conflict Test" con test 201/409.

* Variables de entorno:
  * Añadir `registerEmail` y `registerPassword` en el environment para pruebas de registro.

## Guía de Pruebas en Postman

* Seleccionar entorno "Agenda360Lite Local".
* Flujo sugerido:
  1. Auth → Register (usa `{{registerEmail}}`, `{{registerPassword}}`).
  2. Auth → Login (captura `{{token}}`).
  3. Clients → Create Client; luego List Clients (verifica `meta.total`).
  4. Services → Create Service; luego List Services (verifica `meta.total`).
  5. Appointments → Create Appointment; luego Get by ID.
  6. Appointments → Create Appointment Conflict Test (espera 201/409 según solape).
  7. Appointments → Availability (ver horarios ocupados; siguiente iteración: slots libres).
  8. Appointments → List by range para ventana específica.

## Guía de Pruebas en Android Studio

* Backend activo en `http://localhost:8080`; app usa `http://10.0.2.2:8080/`.
* Login con el usuario de BD; validar:
  * Dashboard lista citas del día.
  * CRUDs (clientes/servicios/citas) responden correctamente y muestran errores 401/409.

## Próximos Pasos (Análisis)

* Validaciones y errores: reforzar formatos y rangos en DTOs; respuestas uniformes.
* Disponibilidad: transformar endpoint para devolver slots libres según `durationMinutes` y horario configurable.
* Estados de citas: endpoint para transiciones válidas `SCHEDULED → DONE/CANCELLED`.
* Actualizar documentación en `docs/` con nuevas rutas y flujos de prueba.

## Criterios de Aceptación

* Register retorna 201 y datos del usuario.
* Login captura token y autoriza CRUDs.
* Listas muestran `data.items` y `data.meta.total`.
* Conflict Test devuelve 201 o 409 según corresponda.
* Availability responde 200 y lista de horas ocupadas por ahora.