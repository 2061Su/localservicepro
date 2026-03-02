package com.example.localserviceapp.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.data.model.Service
import com.example.localserviceapp.viewmodel.ServiceViewModel
import com.example.localserviceapp.viewmodel.ServiceUiState
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditServiceScreen(
    navController: NavController,
    serviceId: String? = null,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf("") }

    val uiState by viewModel.serviceUiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    // Pre-fill data if editing
    LaunchedEffect(serviceId) {
        if (serviceId != null) {
            viewModel.getService(serviceId)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ServiceUiState.ServiceAdded -> {
                navController.popBackStack()
            }
            is ServiceUiState.Success -> {
                val service = (uiState as ServiceUiState.Success).services?.firstOrNull { it.id == serviceId }
                if (service != null) {
                    name = service.name
                    type = service.type
                    price = service.price.toString()
                    phoneNumber = service.phoneNumber
                    description = service.experienceDescription
                    existingImageUrl = service.imageUrl
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (serviceId == null) "Add Service" else "Edit Service") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Service Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Service Type") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Provider Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select Image")
                }
                if (imageUri != null) {
                    Text("New Image Selected", style = MaterialTheme.typography.bodySmall)
                } else if (existingImageUrl.isNotEmpty()) {
                    Text("Existing Image will be preserved", style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val service = Service(
                            id = serviceId ?: UUID.randomUUID().toString(),
                            name = name,
                            type = type,
                            price = price.toDoubleOrNull() ?: 0.0,
                            phoneNumber = phoneNumber,
                            experienceDescription = description,
                            imageUrl = existingImageUrl,
                            createdByAdminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        )
                        if (serviceId == null) {
                            viewModel.addService(service, imageUri)
                        } else {
                            viewModel.updateService(service, imageUri)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is ServiceUiState.Loading && name.isNotBlank() && phoneNumber.isNotBlank()
                ) {
                    Text("Save Service")
                }
                
                if (uiState is ServiceUiState.Error) {
                    Text((uiState as ServiceUiState.Error).message, color = MaterialTheme.colorScheme.error)
                }
            }

            if (uiState is ServiceUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
