package com.example.localserviceapp.viewmodel

import app.cash.turbine.test
import com.example.localserviceapp.data.model.Booking
import com.example.localserviceapp.data.repository.BookingRepository
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
class BookingViewModelTest {

    private lateinit var viewModel: BookingViewModel
    private val repository: BookingRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BookingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBookings success updates state correctly`() = runTest {
        val bookings = listOf(Booking(id = "b1", serviceName = "Plumbing"))
        coEvery { repository.getBookings("user1") } returns flowOf(Result.success(bookings))

        viewModel.getBookings("user1")

        viewModel.bookingUiState.test {
            assertEquals(BookingUiState.Idle, awaitItem())
            assertEquals(BookingUiState.Loading, awaitItem())
            assertEquals(BookingUiState.Success(bookings), awaitItem())
        }
    }

    @Test
    fun `getBookings failure updates state with error`() = runTest {
        val errorMsg = "Network Error"
        coEvery { repository.getBookings(any()) } returns flowOf(Result.failure(Exception(errorMsg)))

        viewModel.getBookings("user1")

        viewModel.bookingUiState.test {
            assertEquals(BookingUiState.Idle, awaitItem())
            assertEquals(BookingUiState.Loading, awaitItem())
            assertEquals(BookingUiState.Error(errorMsg), awaitItem())
        }
    }
}
