package com.example.serviceapp.data.common.database.daos

import androidx.room.*
import com.example.serviceapp.data.common.database.entities.Service
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM service")
    fun getAll(): Flow<List<Service>>

    @Insert
    suspend fun insertAll(vararg services: Service)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(service: Service)

    @Query("DELETE FROM service")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(service: Service)
}