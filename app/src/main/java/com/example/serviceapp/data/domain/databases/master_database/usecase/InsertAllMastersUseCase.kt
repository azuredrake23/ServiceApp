package com.example.serviceapp.data.domain.databases.master_database.usecase

import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.data.domain.databases.master_database.MasterRepository
import javax.inject.Inject

class InsertAllMastersUseCase @Inject constructor (private val masterRepository: MasterRepository){

    suspend fun insertAll(vararg masters: Master) {
        masterRepository.insertAll(*masters)
    }
}