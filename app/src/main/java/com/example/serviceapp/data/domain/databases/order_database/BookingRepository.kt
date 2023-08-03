package com.example.serviceapp.data.domain.databases.order_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Booking

interface BookingRepository {
    fun getBookingDataList(): LiveData<List<Booking>>
    //    fun loadAllByIds(userIds: IntArray)
//    fun findByName(first: String, last: String)
    suspend fun insertAll(vararg bookings: Booking)
    suspend fun insert(booking: Booking)
    suspend fun deleteAll()
    suspend fun delete(booking: Booking)
}