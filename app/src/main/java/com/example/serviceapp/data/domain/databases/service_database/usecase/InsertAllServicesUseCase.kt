package com.example.serviceapp.data.domain.databases.service_database.usecase

import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.data.domain.databases.service_database.ServiceRepository
import javax.inject.Inject

class InsertAllServicesUseCase @Inject constructor (private val serviceRepository: ServiceRepository){

    suspend fun insertAll(vararg services: Service) {
        serviceRepository.insertAll(*services)
    }
}