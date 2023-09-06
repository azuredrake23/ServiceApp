package com.example.serviceapp.domain.databases.master_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Master
import kotlinx.coroutines.flow.Flow


interface MasterRepository {
    fun getMasterDataList(): Flow<List<Master>>
    //    fun loadAllByIds(userIds: IntArray)
//    fun findByName(first: String, last: String)
    suspend fun insertAll(vararg masters: Master)
    suspend fun insert(master: Master)
    suspend fun deleteAll()
    suspend fun delete(master: Master)
}