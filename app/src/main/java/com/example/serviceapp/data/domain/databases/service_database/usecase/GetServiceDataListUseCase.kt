package com.example.serviceapp.data.domain.databases.service_database.usecase

import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.data.domain.databases.service_database.ServiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServiceDataListUseCase @Inject constructor (serviceRepository: ServiceRepository){

    val serviceDataList: Flow<List<Service>> = serviceRepository.getServiceDataList()
}