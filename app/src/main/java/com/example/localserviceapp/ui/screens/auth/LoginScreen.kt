package com.example.localserviceapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.localserviceapp.ui.components.GradientButton
import com.example.localserviceapp.ui.theme.PrimaryGradientEnd
import com.example.localserviceapp.ui.theme.PrimaryGradientStart
import com.example.localserviceapp.viewmodel.AuthViewModel
import com.example.localserviceapp.viewmodel.AuthUiState

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authUiState.collectAsState()

    val softGradient = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF3E5F5)) // White to 5% Light Purple
    )

    Box(modifier = Modifier.fillMaxSize().background(softGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (authState is AuthUiState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    color = PrimaryGradientStart
                )
            }

            Icon(
                imageVector = Icons.Default.BuildCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = PrimaryGradientStart
            )
            
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            GradientButton(
                text = "Login",
                onClick = { authViewModel.login(email.trim(), password.trim()) },
                enabled = authState !is AuthUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("New here? Create an account", color = PrimaryGradientStart)
            }

            TextButton(onClick = { navController.navigate("forgotPassword") }) {
                Text("Forgot Password?", color = Color.Gray)
            }

            if (authState is AuthUiState.Success) {
                val route = (authState as AuthUiState.Success).message
                LaunchedEffect(authState) {
                    if (route == "adminDashboard" || route == "userHome") {
                        navController.navigate(route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            } else if (authState is AuthUiState.Error) {
                Text(
                    text = (authState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
