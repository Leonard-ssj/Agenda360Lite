package seed

import repository.InMemoryStore
import model.Client
import model.ServiceItem
import model.Appointment
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun seedDemo() {
    val ownerId = 1L
    val client = Client(id = InMemoryStore.nextClientId(), name = "Juan Perez", phone = "555-1234", email = "juan@example.com", notes = null, ownerId = ownerId)
    val service = ServiceItem(id = InMemoryStore.nextServiceId(), name = "Corte de cabello", durationMinutes = 30, price = 10.0, description = null, ownerId = ownerId)
    InMemoryStore.clientsByOwner.getOrPut(ownerId) { mutableListOf() }.add(client)
    InMemoryStore.servicesByOwner.getOrPut(ownerId) { mutableListOf() }.add(service)

    val today = Instant.now().atOffset(ZoneOffset.UTC).toLocalDate()
    val dt = DateTimeFormatter.ISO_INSTANT.format(today.atTime(10,0).toInstant(ZoneOffset.UTC))
    val appt = Appointment(id = InMemoryStore.nextAppointmentId(), clientId = client.id, serviceId = service.id, userId = ownerId, dateTime = dt, status = "SCHEDULED", notes = "Demo")
    InMemoryStore.appointmentsByUser.getOrPut(ownerId) { mutableListOf() }.add(appt)
}

