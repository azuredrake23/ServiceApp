package com.example.serviceapp.data.domain.databases.user_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.User

interface UserRepository {

    fun getUserDataList(): LiveData<List<User>>
//    fun loadAllByIds(userIds: IntArray)
    fun getUser(userId: Int): User
    suspend fun insertAll(vararg users: User)
    suspend fun insert(user: User)
    suspend fun deleteAll()
    suspend fun delete(user: User)
}