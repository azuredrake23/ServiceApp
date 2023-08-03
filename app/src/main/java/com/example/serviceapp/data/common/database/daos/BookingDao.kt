package com.example.serviceapp.data.common.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.serviceapp.data.common.database.entities.Booking

@Dao
interface BookingDao {
    @Query("SELECT * FROM booking")
    fun getAll(): LiveData<List<Booking>>

    @Insert
    suspend fun insertAll(vararg bookings: Booking)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(booking: Booking)

    @Query("DELETE FROM booking")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(booking: Booking)
}