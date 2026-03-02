package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun addBooking(booking: Booking): Flow<Result<Unit>>
    fun getBookings(userId: String): Flow<Result<List<Booking>>>
    fun getAllBookings(): Flow<Result<List<Booking>>>
    fun updateBooking(booking: Booking): Flow<Result<Unit>>
    fun updateBookingStatus(bookingId: String, status: String): Flow<Result<Unit>>
    fun deleteBooking(bookingId: String): Flow<Result<Unit>>


}
