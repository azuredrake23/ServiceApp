package com.example.serviceapp.data.common.database.daos

import androidx.room.*

import com.example.serviceapp.data.common.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM user WHERE phoneNumber =:phoneNumber")
    suspend fun isUserExistsByPhone(phoneNumber: String): Int

    @Query("SELECT COUNT(*) FROM user WHERE email =:email")
    suspend fun isUserExistsByEmail(email: String): Int

    @Query("SELECT * FROM user WHERE email =:email OR phoneNumber =:phoneNumber")
    suspend fun getUserData(email: String, phoneNumber: String): User

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE phoneNumber =:phoneNumber")
    fun getUserByPhoneNumber(phoneNumber: String): User?

    @Insert
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(user: User)

    @Transaction
    suspend fun updateAll(vararg users:User){
        deleteAll()
        insertAll(*users)
    }

    @Transaction
    suspend fun update(user: User){
        delete(user)
        insert(user)
    }
}