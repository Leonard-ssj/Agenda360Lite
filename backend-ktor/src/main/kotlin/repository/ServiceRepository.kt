package repository

import model.ServiceItem

class ServiceRepository {
    fun getAllByUser(ownerId: Long): List<ServiceItem> =
        InMemoryStore.servicesByOwner[ownerId]?.toList() ?: emptyList()

    fun create(ownerId: Long, service: ServiceItem): ServiceItem {
        val list = InMemoryStore.servicesByOwner.getOrPut(ownerId) { mutableListOf() }
        val created = service.copy(id = InMemoryStore.nextServiceId(), ownerId = ownerId)
        list.add(created)
        return created
    }

    fun update(ownerId: Long, id: Long, service: ServiceItem): ServiceItem? {
        val list = InMemoryStore.servicesByOwner[ownerId] ?: return null
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val updated = service.copy(id = id, ownerId = ownerId)
            list[idx] = updated
            return updated
        }
        return null
    }

    fun delete(ownerId: Long, id: Long): Boolean {
        val list = InMemoryStore.servicesByOwner[ownerId] ?: return false
        return list.removeIf { it.id == id }
    }
}

