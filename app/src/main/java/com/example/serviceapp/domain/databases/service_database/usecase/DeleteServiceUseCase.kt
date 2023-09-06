package com.example.serviceapp.domain.databases.service_database.usecase

import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.domain.databases.service_database.ServiceRepository
import javax.inject.Inject

class DeleteServiceUseCase @Inject constructor (private val serviceRepository: ServiceRepository) {

    suspend fun delete(service: Service) {
        serviceRepository.delete(service)
    }
}