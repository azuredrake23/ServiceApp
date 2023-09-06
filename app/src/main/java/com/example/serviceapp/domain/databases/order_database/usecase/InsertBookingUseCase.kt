package com.example.serviceapp.domain.databases.order_database.usecase

import com.example.serviceapp.data.common.database.entities.Booking
import com.example.serviceapp.domain.databases.order_database.BookingRepository
import javax.inject.Inject

class InsertBookingUseCase @Inject constructor (private val bookingRepository: BookingRepository){

    suspend fun insert(booking: Booking) {
        bookingRepository.insert(booking)
    }
}