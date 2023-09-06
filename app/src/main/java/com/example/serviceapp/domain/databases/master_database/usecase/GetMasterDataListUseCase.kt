package com.example.serviceapp.domain.databases.master_database.usecase

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.domain.databases.master_database.MasterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMasterDataListUseCase @Inject constructor (masterRepository: MasterRepository){

    val masterDataList: Flow<List<Master>> = masterRepository.getMasterDataList()
}