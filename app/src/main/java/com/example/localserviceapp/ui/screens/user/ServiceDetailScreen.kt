package com.example.localserviceapp.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.localserviceapp.viewmodel.ServiceViewModel
import com.example.localserviceapp.viewmodel.ServiceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    navController: NavController,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val serviceState by viewModel.serviceUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getService(serviceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = serviceState) {
                is ServiceUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ServiceUiState.Success -> {
                    val service = state.services?.firstOrNull { it.id == serviceId }
                    if (service != null) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            AsyncImage(
                                model = service.imageUrl,
                                contentDescription = service.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = service.name, style = MaterialTheme.typography.headlineMedium)
                            Text(text = "Type: ${service.type}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Price: $${service.price}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Contact: ${service.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = service.experienceDescription, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    navController.navigate("bookService/${service.id}/${service.name}?serviceImage=${service.imageUrl}")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Book Now")
                            }
                        }
                    } else {
                        Text("Service not found.", modifier = Modifier.align(Alignment.Center))
                    }
                }
                is ServiceUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                else -> {}
            }
        }
    }
}
