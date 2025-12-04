package com.example.agenda360lite.services.data.repository

import com.example.agenda360lite.core.network.RetrofitClient
import com.example.agenda360lite.services.data.remote.ServiceApi
import com.example.agenda360lite.services.data.remote.PagedService
import com.example.agenda360lite.services.data.remote.Meta
import com.example.agenda360lite.services.data.remote.ServiceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceRepository {
    private val api = RetrofitClient.instance.create(ServiceApi::class.java)

    suspend fun getPaged(q: String?, page: Int?, size: Int?): PagedService = withContext(Dispatchers.IO) {
        val res = api.getPaged(q, page, size)
        res.data ?: PagedService(emptyList(), Meta(page ?: 0, size ?: 20, 0))
    }

    suspend fun create(name: String, durationMinutes: Int, price: Double, description: String?): ServiceItem? = withContext(Dispatchers.IO) {
        val res = api.create(ServiceItem(id = 0, name = name, durationMinutes = durationMinutes, price = price, description = description, ownerId = 0))
        res.data
    }

    suspend fun getById(id: Long): ServiceItem? = withContext(Dispatchers.IO) {
        api.getById(id).data
    }

    suspend fun update(id: Long, name: String, durationMinutes: Int, price: Double, description: String?): ServiceItem? = withContext(Dispatchers.IO) {
        val res = api.update(id, ServiceItem(id = id, name = name, durationMinutes = durationMinutes, price = price, description = description, ownerId = 0))
        res.data
    }
}
