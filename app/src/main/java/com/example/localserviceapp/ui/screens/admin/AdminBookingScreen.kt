package com.example.localserviceapp.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.data.model.BookingStatus
import com.example.localserviceapp.ui.components.BookingCard
import com.example.localserviceapp.viewmodel.BookingViewModel
import com.example.localserviceapp.viewmodel.BookingUiState
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingScreen(
    navController: NavController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookingState = viewModel.bookingUiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.getAllBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Bookings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = bookingState.value) {
                is BookingUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is BookingUiState.Success -> {
                    if (state.bookings.isNullOrEmpty()) {
                        Text("No bookings available.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn (modifier = Modifier.fillMaxSize()) {
                            items(state.bookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    isAdmin = true,
                                    onApprove = {
                                        viewModel.updateBookingStatus(booking.id, BookingStatus.APPROVED)
                                    },
                                    onReject = {
                                        viewModel.updateBookingStatus(booking.id, BookingStatus.REJECTED)
                                    },
                                    onDelete = {
                                        viewModel.deleteBooking(booking.id)
                                    },
                                    onEdit = {
                                        navController.navigate("bookService/${booking.serviceId}/${booking.serviceName}?serviceImage=${booking.serviceImage}&bookingId=${booking.id}")
                                    }
                                )
                            }
                        }
                    }
                }
                is BookingUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
                else -> {}
            }
        }
    }
}
