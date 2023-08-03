package com.example.serviceapp.data.common.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.serviceapp.data.common.database.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<User>>

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