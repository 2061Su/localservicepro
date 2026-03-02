package com.example.localserviceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.localserviceapp.ui.screens.admin.AdminBookingScreen
import com.example.localserviceapp.ui.screens.admin.AdminDashboard
import com.example.localserviceapp.ui.screens.admin.AddEditServiceScreen
import com.example.localserviceapp.ui.screens.auth.ForgotPasswordScreen
import com.example.localserviceapp.ui.screens.auth.LoginScreen
import com.example.localserviceapp.ui.screens.auth.RegisterScreen
import com.example.localserviceapp.ui.screens.auth.SplashScreen
import com.example.localserviceapp.ui.screens.user.BookServiceScreen
import com.example.localserviceapp.ui.screens.user.ServiceDetailScreen
import com.example.localserviceapp.ui.screens.user.UserBookingScreen
import com.example.localserviceapp.ui.screens.user.UserHomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
        composable("userHome") { UserHomeScreen(navController) }
        composable("adminDashboard") { AdminDashboard(navController) }
        composable(
            "addEditService?serviceId={serviceId}",
            arguments = listOf(
                navArgument("serviceId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            AddEditServiceScreen(navController = navController, serviceId = serviceId)
        }
        composable("adminBookings") { AdminBookingScreen(navController) }
        composable(
            "serviceDetail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("serviceId") ?: ""
            ServiceDetailScreen(serviceId = id, navController = navController)
        }
        composable(
            "bookService/{serviceId}/{serviceName}?serviceImage={serviceImage}&bookingId={bookingId}",
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("serviceName") { type = NavType.StringType },
                navArgument("serviceImage") { type = NavType.StringType; nullable = true; defaultValue = "" },
                navArgument("bookingId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            BookServiceScreen(
                serviceId = backStackEntry.arguments?.getString("serviceId") ?: "",
                serviceName = backStackEntry.arguments?.getString("serviceName") ?: "Service",
                serviceImage = backStackEntry.arguments?.getString("serviceImage") ?: "",
                bookingId = backStackEntry.arguments?.getString("bookingId"),
                navController = navController
            )
        }
        composable("userBookings") { UserBookingScreen(navController = navController) }
    }
}
