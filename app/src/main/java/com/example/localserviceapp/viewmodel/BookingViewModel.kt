package com.example.localserviceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localserviceapp.data.model.Booking
import com.example.localserviceapp.data.model.BookingStatus
import com.example.localserviceapp.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Success(val bookings: List<Booking>?) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository
) : ViewModel() {

    private val _bookingUiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingUiState: StateFlow<BookingUiState> = _bookingUiState

    fun addBooking(booking: Booking) {
        repository.addBooking(booking).onEach { result ->
            _bookingUiState.value = when {
                result.isSuccess -> BookingUiState.Success(null)
                result.isFailure -> BookingUiState.Error(result.exceptionOrNull()?.message ?: "An unknown error occurred")
                else -> BookingUiState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun getBookings(userId: String) {
        repository.getBookings(userId).onEach { result ->
            _bookingUiState.value = when {
                result.isSuccess -> BookingUiState.Success(result.getOrNull())
                result.isFailure -> BookingUiState.Error(result.exceptionOrNull()?.message ?: "An unknown error occurred")
                else -> BookingUiState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun getAllBookings() {
        repository.getAllBookings().onEach { result ->
            _bookingUiState.value = when {
                result.isSuccess -> BookingUiState.Success(result.getOrNull())
                result.isFailure -> BookingUiState.Error(result.exceptionOrNull()?.message ?: "An unknown error occurred")
                else -> BookingUiState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        repository.updateBookingStatus(bookingId, status.name).onEach { result ->
            _bookingUiState.value = when {
                result.isSuccess -> {
                    getAllBookings() // Refresh the list
                    BookingUiState.Success(null)
                }
                result.isFailure -> BookingUiState.Error(result.exceptionOrNull()?.message ?: "An unknown error occurred")
                else -> BookingUiState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun deleteBooking(bookingId: String) {
        repository.deleteBooking(bookingId).onEach { result ->
            _bookingUiState.value = when {
                result.isSuccess -> {
                    getAllBookings() // Refresh the list
                    BookingUiState.Success(null)
                }
                result.isFailure -> BookingUiState.Error(result.exceptionOrNull()?.message ?: "An unknown error occurred")
                else -> BookingUiState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun updateBooking(booking: Booking) {
        viewModelScope.launch {
            _bookingUiState.value = BookingUiState.Loading
            repository.updateBooking(booking).collect { result ->
                result.onSuccess {
                    // We set bookings to null here so the UI knows it was a save operation
                    _bookingUiState.value = BookingUiState.Success(null)
                }.onFailure { exception ->
                    _bookingUiState.value = BookingUiState.Error(exception.message ?: "Update Failed")
                }
            }
        }
    }
}
