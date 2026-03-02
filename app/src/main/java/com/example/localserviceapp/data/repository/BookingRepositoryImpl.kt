package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override fun addBooking(booking: Booking): Flow<Result<Unit>> = flow {
        firestore.collection("bookings").document(booking.id).set(booking).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun getBookings(userId: String): Flow<Result<List<Booking>>> = flow {
        val snapshot = firestore.collection("bookings")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val bookings = snapshot.toObjects(Booking::class.java)
        emit(Result.success(bookings))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun getAllBookings(): Flow<Result<List<Booking>>> = flow {
        val snapshot = firestore.collection("bookings")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val bookings = snapshot.toObjects(Booking::class.java)
        emit(Result.success(bookings))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun updateBooking(booking: Booking): Flow<Result<Unit>> = flow {
        firestore.collection("bookings").document(booking.id).set(booking).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun updateBookingStatus(bookingId: String, status: String): Flow<Result<Unit>> = flow {
        firestore.collection("bookings").document(bookingId).update("status", status).await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    override fun deleteBooking(bookingId: String): Flow<Result<Unit>> = flow {
        firestore.collection("bookings").document(bookingId).delete().await()
        emit(Result.success(Unit))
    }.catch { exception ->
        emit(Result.failure(exception))
    }
}
