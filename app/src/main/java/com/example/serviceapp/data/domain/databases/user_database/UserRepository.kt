package com.example.serviceapp.data.domain.databases.user_database

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.ui.fragments.models.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {

    fun getUsersDataList(): Flow<List<User>>
    suspend fun setUserRegisteredState(email: String, password: String)
    fun getUserData(): SharedFlow<UserModel.UserDataWithSignInState>
//    fun loadAllByIds(userIds: IntArray)
    fun getUser(userId: Int): User
    suspend fun insertAll(vararg users: User)
    suspend fun insert(user: User)
    suspend fun deleteAll()
    suspend fun delete(user: User)
}