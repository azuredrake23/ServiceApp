package com.example.serviceapp.domain.databases.order_database

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.daos.BookingDao
import com.example.serviceapp.data.common.database.entities.Booking

class BookingRepositoryImpl(private val bookingDao: BookingDao) : BookingRepository {

    override fun getBookingDataList(): LiveData<List<Booking>> = bookingDao.getAll()

    //    override fun loadAllByIds(userIds: IntArray) {
//        userDao.loadAllByIds(userIds)
//    }
//
//    override fun findByName(first: String, last: String) {
//        userDao.findByName()
//    }

    override suspend fun insertAll(vararg bookings: Booking) {
        bookingDao.insertAll(*bookings)
    }

    override suspend fun insert(booking: Booking) {
        bookingDao.insert(booking)
    }

    override suspend fun deleteAll() {
        bookingDao.deleteAll()
    }

    override suspend fun delete(booking: Booking) {
        bookingDao.delete(booking)
    }

}