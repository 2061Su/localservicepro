package com.example.localserviceapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.viewmodel.AuthViewModel
import com.example.localserviceapp.viewmodel.AuthUiState

@Composable
fun ForgotPasswordScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    val authState = authViewModel.authUiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.forgotPassword(email.trim()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Password Reset Email")
        }

        when (val state = authState.value) {
            is AuthUiState.Loading -> CircularProgressIndicator()
            is AuthUiState.Success -> {
                Text("Password reset email sent successfully.")
            }
            is AuthUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
