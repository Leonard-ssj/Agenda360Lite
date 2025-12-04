package repository

import model.ServiceItem
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Services
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SortOrder

import org.jetbrains.exposed.sql.and

class DbServiceRepository {
    fun getById(ownerId: Long, id: Long): ServiceItem? = transaction {
        Services.select { (Services.id eq id) and (Services.ownerId eq ownerId) }
            .limit(1)
            .firstOrNull()
            ?.let {
                ServiceItem(
                    id = it[Services.id],
                    name = it[Services.name],
                    durationMinutes = it[Services.durationMinutes],
                    price = it[Services.price],
                    description = it[Services.description],
                    ownerId = ownerId
                )
            }
    }
    fun getAllByUser(ownerId: Long): List<ServiceItem> = transaction {
        Services.select { Services.ownerId eq ownerId }.map {
            ServiceItem(
                id = it[Services.id],
                name = it[Services.name],
                durationMinutes = it[Services.durationMinutes],
                price = it[Services.price],
                description = it[Services.description],
                ownerId = ownerId
            )
        }
    }

    fun getPaged(ownerId: Long, q: String?, page: Int, size: Int): List<ServiceItem> = transaction {
        val offset = (page.coerceAtLeast(0)) * size.coerceAtLeast(1)
        val cond = if (q.isNullOrBlank()) (Services.ownerId eq ownerId) else org.jetbrains.exposed.sql.SqlExpressionBuilder.run {
            (Services.ownerId eq ownerId) and (Services.name like "%$q%")
        }
        Services.select { cond }
            .orderBy(Services.id, SortOrder.DESC)
            .limit(size.coerceAtMost(100), offset = offset.toLong())
            .map {
                ServiceItem(
                    id = it[Services.id],
                    name = it[Services.name],
                    durationMinutes = it[Services.durationMinutes],
                    price = it[Services.price],
                    description = it[Services.description],
                    ownerId = ownerId
                )
            }
    }

    fun create(ownerId: Long, service: ServiceItem): ServiceItem = transaction {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val id = Services.insert {
            it[name] = service.name
            it[durationMinutes] = service.durationMinutes
            it[price] = service.price
            it[description] = service.description
            it[Services.ownerId] = ownerId
            it[createdAt] = now
            it[updatedAt] = now
        } get Services.id
        service.copy(id = id, ownerId = ownerId)
    }

    fun update(ownerId: Long, id: Long, service: ServiceItem): ServiceItem? = transaction {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val rows = Services.update({ (Services.id eq id) and (Services.ownerId eq ownerId) }) {
            it[name] = service.name
            it[durationMinutes] = service.durationMinutes
            it[price] = service.price
            it[description] = service.description
            it[updatedAt] = now
        }
        if (rows > 0) service.copy(id = id, ownerId = ownerId) else null
    }

    fun delete(ownerId: Long, id: Long): Boolean = transaction {
        Services.deleteWhere { (Services.id eq id) and (Services.ownerId eq ownerId) } > 0
    }
}
