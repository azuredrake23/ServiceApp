package com.example.serviceapp.data.domain.databases.service_database.usecase

import com.example.serviceapp.data.domain.databases.service_database.ServiceRepository
import javax.inject.Inject

class DeleteAllServicesUseCase @Inject constructor (private val serviceRepository: ServiceRepository) {

    suspend fun deleteAll() {
        serviceRepository.deleteAll()
    }
}