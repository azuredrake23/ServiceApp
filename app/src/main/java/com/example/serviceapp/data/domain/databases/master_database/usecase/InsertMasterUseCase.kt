package com.example.serviceapp.data.domain.databases.master_database.usecase

import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.data.domain.databases.master_database.MasterRepository
import javax.inject.Inject

class InsertMasterUseCase @Inject constructor (private val masterRepository: MasterRepository){

    suspend fun insert(master: Master) {
        masterRepository.insert(master)
    }
}