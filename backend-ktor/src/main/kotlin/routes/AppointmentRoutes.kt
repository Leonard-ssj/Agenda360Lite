package routes

import dto.ResponseEnvelope
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Appointment
import dto.CreateAppointmentRequest
import repository.DbAppointmentRepository
import utils.userIdFromJwt

fun Application.appointmentRoutes() {
    val repo = DbAppointmentRepository()
    routing {
        authenticate {
            route("/api/v1/appointments") {
                put("/{id}/status") {
                    val userId = call.userIdFromJwt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val payload = call.receive<Map<String, String>>()
                    val newStatus = payload["status"] ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Missing status"))
                    val allowed = setOf("SCHEDULED", "DONE", "CANCELLED")
                    if (!allowed.contains(newStatus)) return@put call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid status"))
                    val updated = repo.updateStatus(userId, id, newStatus)
                    if (updated == null) call.respond(HttpStatusCode.NotFound, ResponseEnvelope<Unit>(null, "NOT_FOUND", "Not found"))
                    else call.respond(ResponseEnvelope(updated))
                }
                get("/availability") {
                    val userId = call.userIdFromJwt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val date = call.request.queryParameters["date"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val serviceId = call.request.queryParameters["serviceId"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    try {
                        val slots = repo.getFreeSlots(userId, date, serviceId)
                        call.respond(ResponseEnvelope(slots))
                    } catch (_: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "SERVICE_NOT_FOUND", "Service not found"))
                    }
                }
                get("/{id}") {
                    val userId = call.userIdFromJwt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val item = repo.getById(userId, id)
                    if (item == null) call.respond(HttpStatusCode.NotFound, ResponseEnvelope<Unit>(null, "NOT_FOUND", "Not found"))
                    else call.respond(ResponseEnvelope(item))
                }
                get {
                    val userId = call.userIdFromJwt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val params = call.request.queryParameters
                    val date = params["date"]
                    val from = params["from"]
                    val to = params["to"]
                    if (date != null) {
                        call.respond(ResponseEnvelope(repo.getByDate(userId, date)))
                    } else if (from != null && to != null) {
                        call.respond(ResponseEnvelope(repo.getBetween(userId, from, to)))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Missing ?date=YYYY-MM-DD or ?from&to ISO-8601"))
                    }
                }
                post {
                    val userId = call.userIdFromJwt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val payload = try { call.receive<CreateAppointmentRequest>() } catch (_: Exception) {
                        return@post call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid body"))
                    }
                    if (payload.dateTime.isBlank()) return@post call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "dateTime required"))
                    val appt = Appointment(
                        id = 0L,
                        clientId = payload.clientId,
                        serviceId = payload.serviceId,
                        userId = userId,
                        dateTime = payload.dateTime,
                        status = payload.status,
                        notes = payload.notes
                    )
                    try {
                        val created = repo.create(userId, appt)
                        call.respond(HttpStatusCode.Created, ResponseEnvelope(created))
                    } catch (_: IllegalStateException) {
                        call.respond(HttpStatusCode.Conflict, ResponseEnvelope<Unit>(null, "CONFLICT", "Overlapping appointment"))
                    } catch (_: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "SERVICE_NOT_FOUND", "Service not found"))
                    } catch (_: java.time.format.DateTimeParseException) {
                        call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid dateTime format"))
                    }
                }
                put("/{id}") {
                    val userId = call.userIdFromJwt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val body = call.receive<Appointment>()
                    val updated = repo.update(userId, id, body)
                    if (updated == null) call.respond(HttpStatusCode.Conflict, ResponseEnvelope<Unit>(null, "CONFLICT", "Overlapping appointment or not found"))
                    else call.respond(ResponseEnvelope(updated))
                }
                delete("/{id}") {
                    val userId = call.userIdFromJwt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    val ok = repo.delete(userId, id)
                    if (ok) call.respond(ResponseEnvelope(mapOf("deleted" to true)))
                    else call.respond(HttpStatusCode.NotFound, ResponseEnvelope<Unit>(null, "NOT_FOUND", "Not found"))
                }
            }
        }
    }
}
