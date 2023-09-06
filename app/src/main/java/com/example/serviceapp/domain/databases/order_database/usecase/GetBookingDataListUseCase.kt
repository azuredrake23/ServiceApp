package com.example.serviceapp.domain.databases.order_database.usecase

import androidx.lifecycle.LiveData
import com.example.serviceapp.data.common.database.entities.Booking
import com.example.serviceapp.domain.databases.order_database.BookingRepository
import javax.inject.Inject

class GetBookingDataListUseCase @Inject constructor (bookingRepository: BookingRepository){

    val bookingDataList: LiveData<List<Booking>> = bookingRepository.getBookingDataList()
}