package repository

import model.Appointment
import env.Env
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Appointments
import tables.Services
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Op

class DbAppointmentRepository {
    fun getBetween(userId: Long, from: String, to: String): List<Appointment> = transaction {
        val start = LocalDateTime.parse(from.replace("Z", "")).atOffset(ZoneOffset.UTC).toLocalDateTime()
        val end = LocalDateTime.parse(to.replace("Z", "")).atOffset(ZoneOffset.UTC).toLocalDateTime()
        Appointments.select { (Appointments.userId eq userId) and (Appointments.dateTime.between(start, end)) }
            .orderBy(Appointments.dateTime, SortOrder.ASC)
            .map {
                Appointment(
                    id = it[Appointments.id],
                    clientId = it[Appointments.clientId],
                    serviceId = it[Appointments.serviceId],
                    userId = it[Appointments.userId],
                    dateTime = it[Appointments.dateTime].atOffset(ZoneOffset.UTC).toInstant().toString(),
                    status = it[Appointments.status],
                    notes = it[Appointments.notes]
                )
            }
    }

    private fun getServiceDurationMinutes(serviceId: Long): Int = transaction {
        Services.select { Services.id eq serviceId }
            .limit(1)
            .firstOrNull()?.get(Services.durationMinutes)
            ?: throw IllegalArgumentException("SERVICE_NOT_FOUND")
    }

    fun hasConflict(userId: Long, start: LocalDateTime, end: LocalDateTime, excludeId: Long? = null): Boolean = transaction {
        val join = Appointments.join(Services, JoinType.INNER, Appointments.serviceId, Services.id)
        val cond = (Appointments.userId eq userId) and
                (excludeId?.let { Appointments.id neq it } ?: Op.TRUE)
        join.slice(Appointments.dateTime, Services.durationMinutes).select { cond }.any {
            val otherStart = it[Appointments.dateTime]
            val otherEnd = otherStart.plusMinutes(it[Services.durationMinutes].toLong())
            start.isBefore(otherEnd) && otherStart.isBefore(end)
        }
    }

    fun getFreeSlots(userId: Long, date: String, serviceId: Long): List<String> = transaction {
        val day = LocalDate.parse(date)
        val workStartHour = Env.get("WORK_START", "09")!!.toIntOrNull() ?: 9
        val workEndHour = Env.get("WORK_END", "18")!!.toIntOrNull() ?: 18
        val start = day.atTime(workStartHour, 0)
        val end = day.atTime(workEndHour, 0)
        val dur = getServiceDurationMinutes(serviceId).toLong()
        val slots = mutableListOf<String>()
        var t = start
        while (t.plusMinutes(dur) <= end) {
            val slotStart = t
            val slotEnd = t.plusMinutes(dur)
            val conflict = hasConflict(userId, slotStart, slotEnd)
            if (!conflict) {
                slots.add(slotStart.atOffset(ZoneOffset.UTC).toInstant().toString())
            }
            t = t.plusMinutes(dur)
        }
        slots
    }
    fun getByDate(userId: Long, date: String): List<Appointment> = transaction {
        val day = LocalDate.parse(date)
        val start = day.atStartOfDay()
        val end = day.plusDays(1).atStartOfDay()
        Appointments.select { (Appointments.userId eq userId) and (Appointments.dateTime.between(start, end)) }
            .orderBy(Appointments.dateTime, SortOrder.ASC)
            .map {
                Appointment(
                    id = it[Appointments.id],
                    clientId = it[Appointments.clientId],
                    serviceId = it[Appointments.serviceId],
                    userId = it[Appointments.userId],
                    dateTime = it[Appointments.dateTime].atOffset(ZoneOffset.UTC).toInstant().toString(),
                    status = it[Appointments.status],
                    notes = it[Appointments.notes]
                )
            }
    }

    fun getById(userId: Long, id: Long): Appointment? = transaction {
        Appointments.select { (Appointments.id eq id) and (Appointments.userId eq userId) }
            .limit(1)
            .firstOrNull()
            ?.let {
                Appointment(
                    id = it[Appointments.id],
                    clientId = it[Appointments.clientId],
                    serviceId = it[Appointments.serviceId],
                    userId = it[Appointments.userId],
                    dateTime = it[Appointments.dateTime].atOffset(ZoneOffset.UTC).toInstant().toString(),
                    status = it[Appointments.status],
                    notes = it[Appointments.notes]
                )
            }
    }

    fun create(userId: Long, appt: Appointment): Appointment = transaction {
        val dt = LocalDateTime.parse(appt.dateTime.replace("Z", "")).atOffset(ZoneOffset.UTC).toLocalDateTime()
        val dur = getServiceDurationMinutes(appt.serviceId)
        val end = dt.plusMinutes(dur.toLong())
        if (hasConflict(userId, dt, end)) throw IllegalStateException("CONFLICT")
        val id = Appointments.insert {
            it[clientId] = appt.clientId
            it[serviceId] = appt.serviceId
            it[Appointments.userId] = userId
            it[dateTime] = dt
            it[status] = appt.status
            it[notes] = appt.notes
            it[createdAt] = LocalDateTime.now(ZoneOffset.UTC)
            it[updatedAt] = LocalDateTime.now(ZoneOffset.UTC)
        } get Appointments.id
        appt.copy(id = id, userId = userId)
    }

    fun update(userId: Long, id: Long, appt: Appointment): Appointment? = transaction {
        val dt = LocalDateTime.parse(appt.dateTime.replace("Z", "")).atOffset(ZoneOffset.UTC).toLocalDateTime()
        val dur = getServiceDurationMinutes(appt.serviceId)
        val end = dt.plusMinutes(dur.toLong())
        if (hasConflict(userId, dt, end, excludeId = id)) return@transaction null
        val rows = Appointments.update({ (Appointments.id eq id) and (Appointments.userId eq userId) }) {
            it[clientId] = appt.clientId
            it[serviceId] = appt.serviceId
            it[dateTime] = dt
            it[status] = appt.status
            it[notes] = appt.notes
            it[updatedAt] = LocalDateTime.now(ZoneOffset.UTC)
        }
        if (rows > 0) appt.copy(id = id, userId = userId) else null
    }

    fun delete(userId: Long, id: Long): Boolean = transaction {
        Appointments.deleteWhere { (Appointments.id eq id) and (Appointments.userId eq userId) } > 0
    }

    fun updateStatus(userId: Long, id: Long, newStatus: String): Appointment? = transaction {
        val rows = Appointments.update({ (Appointments.id eq id) and (Appointments.userId eq userId) }) {
            it[status] = newStatus
            it[updatedAt] = LocalDateTime.now(ZoneOffset.UTC)
        }
        if (rows > 0) {
            Appointments.select { (Appointments.id eq id) and (Appointments.userId eq userId) }
                .limit(1)
                .firstOrNull()
                ?.let {
                    Appointment(
                        id = it[Appointments.id],
                        clientId = it[Appointments.clientId],
                        serviceId = it[Appointments.serviceId],
                        userId = it[Appointments.userId],
                        dateTime = it[Appointments.dateTime].atOffset(ZoneOffset.UTC).toInstant().toString(),
                        status = it[Appointments.status],
                        notes = it[Appointments.notes]
                    )
                }
        } else null
    }
}
