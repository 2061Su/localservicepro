package com.example.localserviceapp.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
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
import com.example.localserviceapp.viewmodel.ServiceViewModel
import com.example.localserviceapp.viewmodel.ServiceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController, serviceViewModel: ServiceViewModel = hiltViewModel()) {
    val serviceUiState by serviceViewModel.serviceUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        serviceViewModel.getServices()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Available Services", style = MaterialTheme.typography.headlineMedium) },
                    actions = {
                        IconButton(onClick = { serviceViewModel.logout(navController) }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
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
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already here */ },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("userBookings") },
                    label = { Text("History") },
                    icon = { Icon(Icons.Default.History, null) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Increased padding
        ) {
            when (val state = serviceUiState) {
                is ServiceUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ServiceUiState.Success -> {
                    val services = state.services?.filter { it.isAvailable } ?: emptyList()
                    if (services.isEmpty()) {
                        Text("No available services at the moment.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(services) { service ->
                                ServiceCard(service = service, isAdmin = false) {
                                    navController.navigate("serviceDetail/${service.id}")
                                }
                            }
                        }
                    }
                }
                is ServiceUiState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }
}
