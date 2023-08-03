package com.example.serviceapp.data.domain.databases.master_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Master

interface MasterRepository {
    fun getMasterDataList(): LiveData<List<Master>>
    //    fun loadAllByIds(userIds: IntArray)
//    fun findByName(first: String, last: String)
    suspend fun insertAll(vararg masters: Master)
    suspend fun insert(master: Master)
    suspend fun deleteAll()
    suspend fun delete(master: Master)
}