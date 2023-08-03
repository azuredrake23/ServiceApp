package com.example.serviceapp.data.domain.databases.user_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.daos.UserDao
import com.example.serviceapp.data.common.database.entities.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    override fun getUserDataList(): LiveData<List<User>> = userDao.getAll()

    override fun getUser(userId: Int): User = userDao.getUser(userId)

//    override fun loadAllByIds(userIds: IntArray) {
//        userDao.loadAllByIds(userIds)
//    }
//
//    override fun findByName(first: String, last: String) {
//        userDao.findByName()
//    }

    override suspend fun insertAll(vararg users: User) {
        userDao.insertAll(*users)
    }

    @WorkerThread
    override suspend fun insert(user: User) {
        userDao.insert(user)
    }

    override suspend fun deleteAll() {
        userDao.deleteAll()
    }

    override suspend fun delete(user: User) {
        userDao.delete(user)
    }
}