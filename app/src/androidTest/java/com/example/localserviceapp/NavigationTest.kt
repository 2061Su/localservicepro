package com.example.localserviceapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.localserviceapp.ui.navigation.NavGraph
import com.example.localserviceapp.ui.theme.LocalServiceAppTheme
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splashScreen_navigatesToLogin_afterDelay() {
        // The delay is 2000ms. We wait for it.
        // MainActivity starts at Splash by default in our NavGraph.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Welcome Back").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickRegister_opensRegisterScreen() {
        // Wait for splash transition
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Welcome Back").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Find and click register link
        composeTestRule.onNodeWithText("New here? Create an account").performClick()
        
        // Verify register screen header
        composeTestRule.onNodeWithText("Join Us").assertIsDisplayed()
    }
}
