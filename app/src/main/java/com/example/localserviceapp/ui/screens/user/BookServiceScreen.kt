package com.example.localserviceapp.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.data.model.Booking
import com.example.localserviceapp.data.model.BookingStatus
import com.example.localserviceapp.ui.components.GradientButton
import com.example.localserviceapp.viewmodel.BookingViewModel
import com.example.localserviceapp.viewmodel.BookingUiState
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookServiceScreen(
    serviceId: String,
    serviceName: String,
    serviceImage: String,
    bookingId: String? = null,
    navController: NavController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L

    val uiState by viewModel.bookingUiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is BookingUiState.Success) {
            val successState = uiState as BookingUiState.Success
            if (successState.bookings == null) {
                navController.navigate("userBookings") {
                    popUpTo("userHome")
                }
            } else if (bookingId != null) {
                val booking = successState.bookings?.firstOrNull { it.id == bookingId }
                if (booking != null) {
                    phoneNumber = booking.userPhoneNumber
                    location = booking.userLocation
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (bookingId == null) "Book Service" else "Edit Booking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Service: $serviceName", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Your Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Service Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (selectedDate == 0L) "Select Appointment Date" 
                                   else SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                GradientButton(
                    text = if (bookingId == null) "Book Now" else "Save Changes",
                    onClick = {
                        val user = FirebaseAuth.getInstance().currentUser
                        val booking = Booking(
                            id = bookingId ?: UUID.randomUUID().toString(),
                            userId = user?.uid ?: "",
                            userName = user?.displayName ?: "User",
                            userPhoneNumber = phoneNumber,
                            userLocation = location,
                            serviceId = serviceId,
                            serviceName = serviceName,
                            serviceImage = serviceImage,
                            selectedDate = selectedDate,
                            status = BookingStatus.PENDING.name,
                            createdAt = Calendar.getInstance().time
                        )
                        if (bookingId == null) {
                            viewModel.addBooking(booking)
                        } else {
                            viewModel.updateBooking(booking)
                        }
                    },
                    enabled = uiState !is BookingUiState.Loading && 
                              phoneNumber.isNotBlank() && 
                              location.isNotBlank() &&
                              selectedDate != 0L
                )

                if (uiState is BookingUiState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (uiState as BookingUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (uiState is BookingUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
