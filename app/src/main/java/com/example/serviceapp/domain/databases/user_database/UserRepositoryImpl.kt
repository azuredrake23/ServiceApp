package com.example.serviceapp.domain.databases.user_database

import androidx.annotation.WorkerThread
import com.example.serviceapp.data.common.database.daos.UserDao
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.ui.common_fragments.models.UserModel
import com.example.serviceapp.utils.UserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

class UserRepositoryImpl(private val userDao: UserDao, private val userManager: UserManager) :
    UserRepository {

    override fun getUsersDataList(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserData(): SharedFlow<UserModel.UserDataWithSignInState> = userManager.userDataWithSignInState

    override fun isUserExistsByPhoneNumber(phoneNumber: String): Boolean = userDao.getUserByPhoneNumber(phoneNumber) != null

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

    override suspend fun updateAll(vararg users: User) {
        userDao.updateAll(*users)
    }

    override suspend fun update(user: User) {
        userDao.update(user)
    }
}