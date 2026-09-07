package com.fahim.learncoroutinesbytutorials.ui.timeout

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.utils.FakeApiHelper
import com.fahim.learncoroutinesbytutorials.utils.MainDispatcherRule
import com.fahim.learncoroutinesbytutorials.utils.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenCallSlowerThanTimeout_whenFetch_shouldReturnTimeoutError() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            delayMs = TimeoutViewModel.TIMEOUT_MS * 2
        )

        val viewModel = TimeoutViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Error("TimeoutCancellationException"), viewModel.uiState.value)
    }

    @Test
    fun givenCallFasterThanTimeout_whenFetch_shouldReturnSuccess() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            delayMs = TimeoutViewModel.TIMEOUT_MS / 2
        )

        val viewModel = TimeoutViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Success(TestData.users), viewModel.uiState.value)
    }

    @Test
    fun givenCallFails_whenFetch_shouldReturnGenericError() = runTest {
        val apiHelper = FakeApiHelper(usersError = RuntimeException("boom"))

        val viewModel = TimeoutViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Error("Something Went Wrong"), viewModel.uiState.value)
    }
}
