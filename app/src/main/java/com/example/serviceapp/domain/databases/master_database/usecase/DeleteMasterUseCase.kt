package com.example.serviceapp.domain.databases.master_database.usecase

import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.domain.databases.master_database.MasterRepository
import javax.inject.Inject

class DeleteMasterUseCase @Inject constructor (private val masterRepository: MasterRepository) {

    suspend fun delete(master: Master) {
        masterRepository.delete(master)
    }
}