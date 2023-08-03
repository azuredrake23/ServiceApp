package com.example.serviceapp.data.domain.databases.order_database.usecase

import com.example.serviceapp.data.domain.databases.order_database.BookingRepository
import javax.inject.Inject

class DeleteAllBookingUseCase @Inject constructor (private val bookingRepository: BookingRepository) {

    suspend fun deleteAll() {
        bookingRepository.deleteAll()
    }
}