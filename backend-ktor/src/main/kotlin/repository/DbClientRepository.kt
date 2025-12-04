package repository

import model.Client
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Clients
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SortOrder

import org.jetbrains.exposed.sql.and

class DbClientRepository {
    fun getById(ownerId: Long, id: Long): Client? = transaction {
        Clients.select { (Clients.id eq id) and (Clients.ownerId eq ownerId) }
            .limit(1)
            .firstOrNull()
            ?.let {
                Client(
                    id = it[Clients.id],
                    name = it[Clients.name],
                    phone = it[Clients.phone],
                    email = it[Clients.email],
                    notes = it[Clients.notes],
                    ownerId = ownerId
                )
            }
    }
    fun getAllByUser(ownerId: Long): List<Client> = transaction {
        Clients.select { Clients.ownerId eq ownerId }.map {
            Client(
                id = it[Clients.id],
                name = it[Clients.name],
                phone = it[Clients.phone],
                email = it[Clients.email],
                notes = it[Clients.notes],
                ownerId = ownerId
            )
        }
    }

    fun getPaged(ownerId: Long, q: String?, page: Int, size: Int): List<Client> = transaction {
        val offset = (page.coerceAtLeast(0)) * size.coerceAtLeast(1)
        val cond = if (q.isNullOrBlank()) (Clients.ownerId eq ownerId) else org.jetbrains.exposed.sql.SqlExpressionBuilder.run {
            (Clients.ownerId eq ownerId) and (Clients.name like "%$q%")
        }
        Clients.select { cond }
            .orderBy(Clients.id, SortOrder.DESC)
            .limit(size.coerceAtMost(100), offset = offset.toLong())
            .map {
                Client(
                    id = it[Clients.id],
                    name = it[Clients.name],
                    phone = it[Clients.phone],
                    email = it[Clients.email],
                    notes = it[Clients.notes],
                    ownerId = ownerId
                )
            }
    }

    fun create(ownerId: Long, client: Client): Client = transaction {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val id = Clients.insert {
            it[name] = client.name
            it[phone] = client.phone
            it[email] = client.email
            it[notes] = client.notes
            it[Clients.ownerId] = ownerId
            it[createdAt] = now
            it[updatedAt] = now
        } get Clients.id
        client.copy(id = id, ownerId = ownerId)
    }

    fun update(ownerId: Long, id: Long, client: Client): Client? = transaction {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val rows = Clients.update({ (Clients.id eq id) and (Clients.ownerId eq ownerId) }) {
            it[name] = client.name
            it[phone] = client.phone
            it[email] = client.email
            it[notes] = client.notes
            it[updatedAt] = now
        }
        if (rows > 0) client.copy(id = id, ownerId = ownerId) else null
    }

    fun delete(ownerId: Long, id: Long): Boolean = transaction {
        Clients.deleteWhere { (Clients.id eq id) and (Clients.ownerId eq ownerId) } > 0
    }
}
