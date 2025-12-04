package routes

import dto.ResponseEnvelope
import dto.Paged
import dto.Meta
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.ServiceItem
import repository.DbServiceRepository
import utils.userIdFromJwt

fun Application.serviceRoutes() {
    val repo = DbServiceRepository()
    routing {
        authenticate {
            route("/api/v1/services") {
                get("/{id}") {
                    val userId = call.userIdFromJwt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val item = repo.getById(userId, id)
                    if (item == null) call.respond(HttpStatusCode.NotFound, ResponseEnvelope<Unit>(null, "NOT_FOUND", "Not found"))
                    else call.respond(ResponseEnvelope(item))
                }
                get {
                    val userId = call.userIdFromJwt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val q = call.request.queryParameters["q"]
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    val list = if (q == null && page == 0 && size == 20) repo.getAllByUser(userId) else repo.getPaged(userId, q, page, size)
                    val total = repo.getAllByUser(userId).size.toLong()
                    call.respond(ResponseEnvelope(Paged(items = list, meta = Meta(page, size, total))))
                }
                post {
                    val userId = call.userIdFromJwt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val body = call.receive<ServiceItem>()
                    if (body.name.isBlank() || body.durationMinutes <= 0 || body.price < 0.0)
                        return@post call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid service fields"))
                    val created = repo.create(userId, body)
                    call.respond(HttpStatusCode.Created, ResponseEnvelope(created))
                }
                put("/{id}") {
                    val userId = call.userIdFromJwt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val body = call.receive<ServiceItem>()
                    if (body.name.isBlank() || body.durationMinutes <= 0 || body.price < 0.0)
                        return@put call.respond(HttpStatusCode.BadRequest, ResponseEnvelope<Unit>(null, "BAD_REQUEST", "Invalid service fields"))
                    val updated = repo.update(userId, id, body)
                    if (updated == null) call.respond(HttpStatusCode.NotFound, ResponseEnvelope<Unit>(null, "NOT_FOUND", "Not found"))
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
