package repository

import model.Appointment

class AppointmentRepository {
    fun getByDate(userId: Long, date: String): List<Appointment> {
        val list = InMemoryStore.appointmentsByUser[userId] ?: return emptyList()
        return list.filter { it.dateTime.startsWith(date) }
    }

    fun getById(userId: Long, id: Long): Appointment? {
        val list = InMemoryStore.appointmentsByUser[userId] ?: return null
        return list.find { it.id == id }
    }

    fun create(userId: Long, appt: Appointment): Appointment {
        val list = InMemoryStore.appointmentsByUser.getOrPut(userId) { mutableListOf() }
        val created = appt.copy(id = InMemoryStore.nextAppointmentId(), userId = userId)
        list.add(created)
        return created
    }

    fun update(userId: Long, id: Long, appt: Appointment): Appointment? {
        val list = InMemoryStore.appointmentsByUser[userId] ?: return null
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val updated = appt.copy(id = id, userId = userId)
            list[idx] = updated
            return updated
        }
        return null
    }

    fun delete(userId: Long, id: Long): Boolean {
        val list = InMemoryStore.appointmentsByUser[userId] ?: return false
        return list.removeIf { it.id == id }
    }
}

