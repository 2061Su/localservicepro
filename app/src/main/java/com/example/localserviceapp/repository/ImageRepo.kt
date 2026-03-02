package com.example.localserviceapp.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface ImageRepo {
    fun uploadImage(imageUri: Uri): Flow<Result<String>>
}
