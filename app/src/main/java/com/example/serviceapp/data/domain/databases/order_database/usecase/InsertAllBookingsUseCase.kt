package com.example.serviceapp.data.domain.databases.order_database.usecase

import com.example.serviceapp.data.common.database.entities.Booking
import com.example.serviceapp.data.domain.databases.order_database.BookingRepository
import javax.inject.Inject

class InsertAllBookingsUseCase @Inject constructor (private val bookingRepository: BookingRepository){

    suspend fun insertAll(vararg bookings: Booking) {
        bookingRepository.insertAll(*bookings)
    }
}