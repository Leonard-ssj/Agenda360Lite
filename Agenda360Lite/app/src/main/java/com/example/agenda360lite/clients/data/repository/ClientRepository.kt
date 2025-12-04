package com.example.agenda360lite.clients.data.repository

import com.example.agenda360lite.clients.data.remote.PagedClient
import com.example.agenda360lite.clients.data.remote.Meta
import com.example.agenda360lite.clients.data.remote.ClientApi
import com.example.agenda360lite.clients.data.remote.Client
import com.example.agenda360lite.core.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientRepository {
    private val api = RetrofitClient.instance.create(ClientApi::class.java)

    suspend fun getPaged(q: String?, page: Int?, size: Int?): PagedClient = withContext(Dispatchers.IO) {
        val res = api.getPaged(q, page, size)
        res.data ?: PagedClient(emptyList(), Meta(page ?: 0, size ?: 20, 0))
    }

    suspend fun create(name: String, phone: String?, email: String?, notes: String?): Client? = withContext(Dispatchers.IO) {
        val res = api.create(Client(id = 0, name = name, phone = phone, email = email, notes = notes, ownerId = 0))
        res.data
    }

    suspend fun getById(id: Long): Client? = withContext(Dispatchers.IO) {
        api.getById(id).data
    }

    suspend fun update(id: Long, name: String, phone: String?, email: String?, notes: String?): Client? = withContext(Dispatchers.IO) {
        val res = api.update(id, Client(id = id, name = name, phone = phone, email = email, notes = notes, ownerId = 0))
        res.data
    }
}
