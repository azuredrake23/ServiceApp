package com.example.serviceapp.data.domain.databases.master_database.usecase

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.data.domain.databases.master_database.MasterRepository
import javax.inject.Inject

class GetMasterDataListUseCase @Inject constructor (masterRepository: MasterRepository){

    val masterDataList: LiveData<List<Master>> = masterRepository.getMasterDataList()
}