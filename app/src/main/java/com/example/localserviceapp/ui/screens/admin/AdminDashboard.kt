package com.example.localserviceapp.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.ui.components.ServiceCard
import com.example.localserviceapp.viewmodel.AuthViewModel
import com.example.localserviceapp.viewmodel.ServiceViewModel
import com.example.localserviceapp.viewmodel.ServiceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavController,
    serviceViewModel: ServiceViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val serviceState by serviceViewModel.serviceUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        serviceViewModel.getServices()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium) },
                    actions = {
                        IconButton(onClick = { navController.navigate("adminBookings") }) {
                            Icon(Icons.Default.ListAlt, contentDescription = "View Bookings")
                        }
                        IconButton(onClick = {
                            authViewModel.logoutUser()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.White,
                        scrolledContainerColor = Color.White
                    ),
                    scrollBehavior = scrollBehavior
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addEditService") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Service")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            when (val state = serviceState) {
                is ServiceUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ServiceUiState.Success -> {
                    if (state.services.isNullOrEmpty()) {
                        Text("No services found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(state.services) { service ->
                                ServiceCard(
                                    service = service,
                                    isAdmin = true,
                                    onEdit = {
                                        navController.navigate("addEditService?serviceId=${service.id}")
                                    },
                                    onDelete = {
                                        serviceViewModel.deleteService(service.id)
                                    },
                                    onClick = {}
                                )
                            }
                        }
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
