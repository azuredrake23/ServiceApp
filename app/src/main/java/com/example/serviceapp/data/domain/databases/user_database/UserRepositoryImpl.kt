package com.example.serviceapp.data.domain.databases.user_database

import androidx.annotation.WorkerThread
import com.example.serviceapp.data.common.database.daos.UserDao
import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.ui.fragments.models.UserModel
import com.example.serviceapp.utils.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val userDao: UserDao, private val userManager: UserManager) :
    UserRepository {

    override fun getUsersDataList(): Flow<List<User>> = userDao.getAllUsers()

    override suspend fun setUserRegisteredState(email: String, password: String) {
        withContext(Dispatchers.IO) {
            userManager.setUserDataWithSignInState(
                userDao.userRegistration(email, password) > 0,
                userDao.incorrectPass(email, password) > 0,
                userDao.getUserData(email, password)
            )
        }
    }

    override fun getUserData(): SharedFlow<UserModel.UserDataWithSignInState> = userManager.userDataWithSignInState

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