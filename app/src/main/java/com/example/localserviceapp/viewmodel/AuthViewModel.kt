package com.example.localserviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localserviceapp.data.model.User
import com.example.localserviceapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

// FIXED: Success now takes a String so we can pass Routes or Success Messages
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    fun register(user: User, password: String) {
        _authUiState.value = AuthUiState.Loading
        repository.registerUser(user, password).onEach { result ->
            if (result.isSuccess) {
                // Navigate to login after successful registration
                _authUiState.value = AuthUiState.Success("login")
            } else {
                _authUiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }.launchIn(viewModelScope)
    }

    fun login(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        repository.loginUser(email, password).onEach { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()
                val route = if (user?.role == "ADMIN") "adminDashboard" else "userHome"
                _authUiState.value = AuthUiState.Success(route)
            } else {
                _authUiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login Failed")
            }
        }.launchIn(viewModelScope)
    }

    fun forgotPassword(email: String) {
        _authUiState.value = AuthUiState.Loading
        repository.forgotPassword(email).onEach { result ->
            if (result.isSuccess) {
                _authUiState.value = AuthUiState.Success("Reset Link Sent")
            } else {
                _authUiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        repository.logoutUser() // Assuming this is in your Repository
        _authUiState.value = AuthUiState.Idle
    }

    // In AuthViewModel.kt
    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
    }
}