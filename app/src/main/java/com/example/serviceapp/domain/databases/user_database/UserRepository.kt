package com.example.serviceapp.domain.databases.user_database

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.ui.common_fragments.models.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {

    fun getUsersDataList(): Flow<List<User>>
    fun getUserData(): SharedFlow<UserModel.UserDataWithSignInState>
//    fun loadAllByIds(userIds: IntArray)
    fun isUserExistsByPhoneNumber(phoneNumber: String): Boolean
    suspend fun insertAll(vararg users: User)
    suspend fun insert(user: User)
    suspend fun deleteAll()
    suspend fun delete(user: User)
    suspend fun updateAll(vararg users: User)
    suspend fun update(user:User)
}