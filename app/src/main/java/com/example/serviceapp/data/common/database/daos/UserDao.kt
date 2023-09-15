package com.example.serviceapp.data.common.database.daos

import androidx.room.*

import com.example.serviceapp.data.common.database.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.http.DELETE
import java.nio.file.Files.delete

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM user WHERE phoneNumber =:phoneNumber")
    suspend fun isUserExistsByPhone(phoneNumber: String): Int

    @Query("SELECT COUNT(*) FROM user WHERE email =:email")
    suspend fun isUserExistsByEmail(email: String): Int

    @Query("SELECT * FROM user WHERE email =:email OR phoneNumber =:phoneNumber")
    suspend fun getUser(email: String? = null, phoneNumber: String? = null): User?

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>

//    @Insert
//    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)


//    @Transaction
//    suspend fun updateAll(vararg users:User){
//        deleteAll()
//        insertAll(*users)
//    }

    @Transaction
    suspend fun update(oldEmail: String, oldPhoneNumber: String, user: User){
        delete(oldEmail, oldPhoneNumber)
        insert(user)
    }

    @Query ("DELETE FROM user WHERE email =:email OR phoneNumber =:phoneNumber")
    suspend fun delete(email: String? = null, phoneNumber: String? = null)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}