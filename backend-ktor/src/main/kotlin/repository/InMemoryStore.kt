package repository

import model.Client
import model.ServiceItem
import model.Appointment
import java.util.concurrent.atomic.AtomicLong

object InMemoryStore {
    private val clientSeq = AtomicLong(1)
    private val serviceSeq = AtomicLong(1)
    private val appointmentSeq = AtomicLong(1)

    val clientsByOwner: MutableMap<Long, MutableList<Client>> = mutableMapOf()
    val servicesByOwner: MutableMap<Long, MutableList<ServiceItem>> = mutableMapOf()
    val appointmentsByUser: MutableMap<Long, MutableList<Appointment>> = mutableMapOf()

    fun nextClientId() = clientSeq.getAndIncrement()
    fun nextServiceId() = serviceSeq.getAndIncrement()
    fun nextAppointmentId() = appointmentSeq.getAndIncrement()
}

