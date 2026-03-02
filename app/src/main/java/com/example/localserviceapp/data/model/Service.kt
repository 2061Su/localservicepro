package com.example.localserviceapp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Service(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val type: String = "",
    val price: Double = 0.0,
    val phoneNumber: String = "", // Provider phone number
    val experienceDescription: String = "",
    val createdByAdminId: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val isAvailable: Boolean = true
)
