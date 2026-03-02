package com.example.localserviceapp.viewmodel

import app.cash.turbine.test
import com.example.localserviceapp.data.model.User
import com.example.localserviceapp.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val repository: AuthRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success for USER role updates state correctly`() = runTest {
        val user = User(uid = "123", role = "USER")
        coEvery { repository.loginUser("test@test.com", "password") } returns flowOf(Result.success(user))

        viewModel.login("test@test.com", "password")

        viewModel.authUiState.test {
            // First item is Idle because we are using StateFlow and the collector starts
            assertEquals(AuthUiState.Idle, awaitItem())
            // Then it goes to Loading
            assertEquals(AuthUiState.Loading, awaitItem())
            // Then Success
            assertEquals(AuthUiState.Success("userHome"), awaitItem())
        }
    }

    @Test
    fun `login failure updates state with error`() = runTest {
        val errorMsg = "Login Failed"
        coEvery { repository.loginUser(any(), any()) } returns flowOf(Result.failure(Exception(errorMsg)))

        viewModel.login("bad@email.com", "wrong")

        viewModel.authUiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())
            assertEquals(AuthUiState.Loading, awaitItem())
            assertEquals(AuthUiState.Error(errorMsg), awaitItem())
        }
    }
}
