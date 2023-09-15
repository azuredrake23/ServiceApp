package com.example.serviceapp.domain.databases.user_database

import androidx.annotation.WorkerThread
import com.example.serviceapp.data.common.database.daos.UserDao
import com.example.serviceapp.data.common.database.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    @WorkerThread
    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    @WorkerThread
    override suspend fun isUserExistsByPhone(phoneNumber: String): Boolean =
        userDao.isUserExistsByPhone(phoneNumber) != 0

    @WorkerThread
    override suspend fun isUserExistsByEmail(email: String): Boolean =
        userDao.isUserExistsByEmail(email) != 0

    @WorkerThread
    override suspend fun getUser(email: String?, phoneNumber: String?): User? =
        userDao.getUser(email, phoneNumber)


//    override fun loadAllByIds(userIds: IntArray) {
//        userDao.loadAllByIds(userIds)
//    }
//
//    override fun findByName(first: String, last: String) {
//        userDao.findByName()
//    }

//    @WorkerThread
//    override suspend fun insertAll(vararg users: User) {
//        userDao.insertAll(*users)
//    }

    @WorkerThread
    override suspend fun insert(user: User) {
        userDao.insert(user)
    }

    @WorkerThread
    override suspend fun update(
        oldEmail: String,
        oldPhoneNumber: String,
        user: User
    ) {
        userDao.update(oldEmail, oldPhoneNumber, user)
    }

    @WorkerThread
    override suspend fun delete(email: String?, phoneNumber: String?) {
        userDao.delete(email, phoneNumber)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        userDao.deleteAll()
    }

}