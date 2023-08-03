package com.example.serviceapp.data.common.database.daos

import androidx.room.*

import com.example.serviceapp.data.common.database.entities.User
import com.example.serviceapp.ui.fragments.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM user WHERE email =:email AND password =:password")
    suspend fun userRegistration(email: String, password: String): Int

    @Query("SELECT COUNT(*) FROM user WHERE email =:email AND password !=:password")
    suspend fun incorrectPass(email: String, password: String): Int

    @Query("SELECT * FROM user WHERE email =:email AND password =:password")
    suspend fun getUserData(email: String, password: String): User

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE id =:userID")
    fun getUser(userID: Int): User

    @Insert
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(user: User)
}