package repository

import model.Client

class ClientRepository {
    fun getAllByUser(ownerId: Long): List<Client> =
        InMemoryStore.clientsByOwner[ownerId]?.toList() ?: emptyList()

    fun create(ownerId: Long, client: Client): Client {
        val list = InMemoryStore.clientsByOwner.getOrPut(ownerId) { mutableListOf() }
        val created = client.copy(id = InMemoryStore.nextClientId(), ownerId = ownerId)
        list.add(created)
        return created
    }

    fun update(ownerId: Long, id: Long, client: Client): Client? {
        val list = InMemoryStore.clientsByOwner[ownerId] ?: return null
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val updated = client.copy(id = id, ownerId = ownerId)
            list[idx] = updated
            return updated
        }
        return null
    }

    fun delete(ownerId: Long, id: Long): Boolean {
        val list = InMemoryStore.clientsByOwner[ownerId] ?: return false
        return list.removeIf { it.id == id }
    }
}

