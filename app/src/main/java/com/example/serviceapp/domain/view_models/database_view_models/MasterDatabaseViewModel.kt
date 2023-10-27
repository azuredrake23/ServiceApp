package com.example.serviceapp.domain.view_models.database_view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.common.database.entities.Master
import com.example.serviceapp.domain.databases.master_database.usecase.DeleteAllMastersUseCase
import com.example.serviceapp.domain.databases.master_database.usecase.DeleteMasterUseCase
import com.example.serviceapp.domain.databases.master_database.usecase.GetMasterDataListUseCase
import com.example.serviceapp.domain.databases.master_database.usecase.InsertAllMastersUseCase
import com.example.serviceapp.domain.databases.master_database.usecase.InsertMasterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MasterDatabaseViewModel @Inject constructor(
    private val insertMasterUseCase: InsertMasterUseCase,
    private val insertAllMastersUseCase: InsertAllMastersUseCase,
    getMasterDataListUseCase: GetMasterDataListUseCase,
    private val deleteMasterUseCase: DeleteMasterUseCase,
    private val deleteAllMastersUseCase: DeleteAllMastersUseCase
) : ViewModel() {

    val allMasters: Flow<List<Master>> = getMasterDataListUseCase.masterDataList

    fun insert(master: Master) = viewModelScope.launch {
        insertMasterUseCase.insert(master)
    }

    fun insertAll(vararg masters: Master) = viewModelScope.launch {
        insertAllMastersUseCase.insertAll(*masters)
    }

    fun delete(master: Master) = viewModelScope.launch {
        deleteMasterUseCase.delete(master)
    }

    fun deleteAll() = viewModelScope.launch {
        deleteAllMastersUseCase.deleteAll()
    }
}