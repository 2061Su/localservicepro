package com.example.localserviceapp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoneNumber: String = "", // New field
    val userLocation: String = "",    // New field
    val serviceId: String = "",
    val serviceName: String = "",
    val serviceImage: String = "",
    val dateTime: Long = 0L,          // This can be createdAt timestamp as Long
    val selectedDate: Long = 0L,      // The date selected by user
    val status: String = BookingStatus.PENDING.name,
    @ServerTimestamp
    val createdAt: Date? = null
)
