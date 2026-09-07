package com.fahim.learncoroutinesbytutorials.ui.retrofit.single

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
class SingleNetworkCallViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users)

        val viewModel = SingleNetworkCallViewModel(apiHelper)
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(1, apiHelper.getUsersCalls)
        assertEquals(UiState.Success(TestData.users), viewModel.uiState.value)
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() = runTest {
        val error = RuntimeException("Error Message For You")
        val apiHelper = FakeApiHelper(usersError = error)

        val viewModel = SingleNetworkCallViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(1, apiHelper.getUsersCalls)
        assertEquals(UiState.Error(error.toString()), viewModel.uiState.value)
    }
}
