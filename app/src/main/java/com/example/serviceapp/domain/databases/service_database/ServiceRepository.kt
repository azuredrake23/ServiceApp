package com.example.serviceapp.domain.databases.service_database

import com.example.serviceapp.data.common.database.entities.Service
import kotlinx.coroutines.flow.Flow

interface   ServiceRepository {
    fun getServiceDataList(): Flow<List<Service>>
    //    fun loadAllByIds(userIds: IntArray)
//    fun findByName(first: String, last: String)
    suspend fun insertAll(vararg services: Service)
    suspend fun insert(service: Service)
    suspend fun deleteAll()
    suspend fun delete(service: Service)
}