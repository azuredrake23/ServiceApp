package com.example.serviceapp.data.common.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.serviceapp.data.common.database.entities.Master
import kotlinx.coroutines.flow.Flow

@Dao
interface MasterDao {
    @Query("SELECT * FROM master")
    fun getAll(): Flow<List<Master>>

    @Insert
    suspend fun insertAll(vararg masters: Master)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(master: Master)

    @Query("DELETE FROM master")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(master: Master)
}