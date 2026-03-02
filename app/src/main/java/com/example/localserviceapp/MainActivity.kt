package com.example.localserviceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.localserviceapp.ui.navigation.NavGraph
import com.example.localserviceapp.ui.theme.LocalServiceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.cloudinary.android.MediaManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = mapOf(
            "cloud_name" to "dfm1felvx",
            "api_key" to "592265235611344",
            "api_secret" to "gBfRjd-oyItnDi5XMcRoDeCV5CQ"
        )

        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // This prevents a crash if init is called twice during a demo
        }

        setContent {
            LocalServiceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
