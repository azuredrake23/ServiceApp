package com.example.serviceapp.ui.view_models.database_view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.common.database.entities.Service
import com.example.serviceapp.domain.databases.service_database.usecase.DeleteAllServicesUseCase
import com.example.serviceapp.domain.databases.service_database.usecase.DeleteServiceUseCase
import com.example.serviceapp.domain.databases.service_database.usecase.GetServiceDataListUseCase
import com.example.serviceapp.domain.databases.service_database.usecase.InsertAllServicesUseCase
import com.example.serviceapp.domain.databases.service_database.usecase.InsertServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDatabaseViewModel @Inject constructor(
    private val insertServiceUseCase: InsertServiceUseCase,
    private val insertAllServicesUseCase: InsertAllServicesUseCase,
    getServiceDataListUseCase: GetServiceDataListUseCase,
    private val deleteServiceUseCase: DeleteServiceUseCase,
    private val deleteAllServiceUseCase: DeleteAllServicesUseCase
) : ViewModel() {

    val allServices: Flow<List<Service>> = getServiceDataListUseCase.serviceDataList

    fun insert(service: Service) = viewModelScope.launch {
        insertServiceUseCase.insert(service)
    }

    fun insertAll(vararg services: Service) = viewModelScope.launch {
        insertAllServicesUseCase.insertAll(*services)
    }

    fun delete(service: Service) = viewModelScope.launch {
        deleteServiceUseCase.delete(service)
    }

    fun deleteAll() = viewModelScope.launch {
        deleteAllServiceUseCase.deleteAll()
    }
}