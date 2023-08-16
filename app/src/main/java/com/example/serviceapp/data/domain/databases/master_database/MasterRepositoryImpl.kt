package com.example.serviceapp.data.domain.databases.master_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.daos.MasterDao
import com.example.serviceapp.data.common.database.entities.Master
import kotlinx.coroutines.flow.Flow

class MasterRepositoryImpl(private val masterDao: MasterDao) : MasterRepository {

    override fun getMasterDataList(): Flow<List<Master>> = masterDao.getAll()

    override suspend fun insertAll(vararg masters: Master) {
        masterDao.insertAll(*masters)
    }

    override suspend fun insert(master: Master) {
        masterDao.insert(master)
    }

    //    override fun loadAllByIds(userIds: IntArray) {
//        userDao.loadAllByIds(userIds)
//    }
//
//    override fun findByName(first: String, last: String) {
//        userDao.findByName()
//    }

    override suspend fun deleteAll() {
        masterDao.deleteAll()
    }

    override suspend fun delete(master: Master) {
        masterDao.delete(master)
    }

}