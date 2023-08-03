package com.example.serviceapp.data.domain.databases.service_database

import androidx.annotation.WorkerThread
import com.example.serviceapp.data.common.database.daos.ServiceDao
import com.example.serviceapp.data.common.database.entities.Service
import kotlinx.coroutines.flow.Flow

class ServiceRepositoryImpl(private val serviceDao: ServiceDao) : ServiceRepository {

    override fun getServiceDataList(): Flow<List<Service>> = serviceDao.getAll()

//    override fun loadAllByIds(userIds: IntArray) {
//        userDao.loadAllByIds(userIds)
//    }
//
//    override fun findByName(first: String, last: String) {
//        userDao.findByName()
//    }

    override suspend fun insertAll(vararg services: Service) {
        serviceDao.insertAll(*services)
    }

    @WorkerThread
    override suspend fun insert(service: Service) {
        serviceDao.insert(service)
    }

    override suspend fun deleteAll() {
        serviceDao.deleteAll()
    }

    override suspend fun delete(service: Service) {
        serviceDao.delete(service)
    }

}