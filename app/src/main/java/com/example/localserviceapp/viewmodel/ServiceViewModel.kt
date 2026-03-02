package com.example.localserviceapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.localserviceapp.data.model.Service
import com.example.localserviceapp.data.repository.ServiceRepository
import com.example.localserviceapp.repository.ImageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ServiceUiState {
    object Idle : ServiceUiState()
    object Loading : ServiceUiState()
    object ServiceAdded : ServiceUiState()
    data class Success(val services: List<Service>?) : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()
}

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: ServiceRepository,
    private val imageRepo: ImageRepo
) : ViewModel() {

    private val _serviceUiState = MutableStateFlow<ServiceUiState>(ServiceUiState.Idle)
    val serviceUiState: StateFlow<ServiceUiState> = _serviceUiState

    fun getServices() {
        viewModelScope.launch {
            _serviceUiState.value = ServiceUiState.Loading
            repository.getServices().collect { result ->
                result.onSuccess { services ->
                    _serviceUiState.value = ServiceUiState.Success(services)
                }.onFailure { error ->
                    _serviceUiState.value = ServiceUiState.Error(error.message ?: "Unknown Error")
                }
            }
        }
    }

    fun getService(id: String) {
        viewModelScope.launch {
            _serviceUiState.value = ServiceUiState.Loading
            repository.getService(id).collect { result ->
                result.onSuccess { service ->
                    _serviceUiState.value = ServiceUiState.Success(if (service != null) listOf(service) else emptyList())
                }.onFailure { error ->
                    _serviceUiState.value = ServiceUiState.Error(error.message ?: "Unknown Error")
                }
            }
        }
    }

    fun addService(service: Service, imageUri: Uri?) {
        viewModelScope.launch {
            _serviceUiState.value = ServiceUiState.Loading

            if (imageUri != null) {
                imageRepo.uploadImage(imageUri).collect { result ->
                    result.onSuccess { secureUrl ->
                        val finalService = service.copy(imageUrl = secureUrl)
                        saveToFirestore(finalService)
                    }.onFailure {
                        _serviceUiState.value = ServiceUiState.Error("Image Upload Failed")
                    }
                }
            } else {
                saveToFirestore(service)
            }
        }
    }

    fun updateService(service: Service, imageUri: Uri?) {
        viewModelScope.launch {
            _serviceUiState.value = ServiceUiState.Loading

            if (imageUri != null) {
                imageRepo.uploadImage(imageUri).collect { result ->
                    result.onSuccess { secureUrl ->
                        val finalService = service.copy(imageUrl = secureUrl)
                        updateInFirestore(finalService)
                    }.onFailure {
                        _serviceUiState.value = ServiceUiState.Error("Image Upload Failed")
                    }
                }
            } else {
                updateInFirestore(service)
            }
        }
    }

    private suspend fun updateInFirestore(service: Service) {
        repository.updateService(service).collect { result ->
            if (result.isSuccess) {
                _serviceUiState.value = ServiceUiState.ServiceAdded
                getServices()
            } else {
                _serviceUiState.value = ServiceUiState.Error("Firestore Update Failed")
            }
        }
    }

    private suspend fun saveToFirestore(service: Service) {
        repository.addService(service).collect { result ->
            if (result.isSuccess) {
                _serviceUiState.value = ServiceUiState.ServiceAdded
                getServices()
            } else {
                _serviceUiState.value = ServiceUiState.Error("Firestore Save Failed")
            }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            repository.deleteService(serviceId).collect { result ->
                if (result.isSuccess) {
                    getServices()
                }
            }
        }
    }

    fun logout(navController: NavController) {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        navController.navigate("login") { popUpTo(0) }
    }
}
