package com.example.serviceapp.domain.databases.user_database

import com.example.serviceapp.data.common.database.daos.UserDao
import com.example.serviceapp.data.common.database.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>

    suspend fun isUserExistsByPhone(phoneNumber: String): Boolean

    suspend fun isUserExistsByEmail(email: String): Boolean

    suspend fun getUser(email: String? = null, phoneNumber: String? = null): User?

    suspend fun insert(user: User)

    suspend fun update(oldEmail: String, oldPhoneNumber: String, user: User)

    suspend fun delete(email: String? = null, phoneNumber: String? = null)

    suspend fun deleteAll()
}