package com.example.serviceapp.data.domain.databases.master_database.usecase

import com.example.serviceapp.data.domain.databases.master_database.MasterRepository
import javax.inject.Inject

class DeleteAllMastersUseCase @Inject constructor (private val masterRepository: MasterRepository) {

    suspend fun deleteAll() {
        masterRepository.deleteAll()
    }
}