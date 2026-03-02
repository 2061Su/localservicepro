package com.example.localserviceapp.data.repository

import com.example.localserviceapp.data.model.User
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun registerUser(user: User, password: String): Flow<Result<AuthResult>>
    fun loginUser(email: String, password: String): Flow<Result<User>>
    fun logoutUser()
    fun forgotPassword(email: String): Flow<Result<Unit>>
}
