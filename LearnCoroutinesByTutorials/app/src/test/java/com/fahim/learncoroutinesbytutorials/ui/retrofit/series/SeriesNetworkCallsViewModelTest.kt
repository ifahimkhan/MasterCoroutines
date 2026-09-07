package com.fahim.learncoroutinesbytutorials.ui.retrofit.series

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.utils.FakeApiHelper
import com.fahim.learncoroutinesbytutorials.utils.MainDispatcherRule
import com.fahim.learncoroutinesbytutorials.utils.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeriesNetworkCallsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenBothCallsSucceed_whenFetch_shouldCombineInOrder() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users, moreUsers = TestData.moreUsers)

        val viewModel = SeriesNetworkCallsViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Success(TestData.users + TestData.moreUsers), viewModel.uiState.value)
    }

    @Test
    fun givenEachCallTakes1s_whenFetch_shouldRunSequentiallyTaking2s() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            moreUsers = TestData.moreUsers,
            delayMs = 1000
        )

        val viewModel = SeriesNetworkCallsViewModel(apiHelper)

        advanceTimeBy(1500)
        assertEquals(UiState.Loading, viewModel.uiState.value)
        assertEquals(1, apiHelper.getUsersCalls)
        assertEquals(1, apiHelper.getMoreUsersCalls)

        advanceTimeBy(600)
        assertEquals(UiState.Success(TestData.users + TestData.moreUsers), viewModel.uiState.value)
    }

    @Test
    fun givenSecondCallFails_whenFetch_shouldReturnError() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            moreUsersError = RuntimeException("boom")
        )

        val viewModel = SeriesNetworkCallsViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Error("Something Went Wrong"), viewModel.uiState.value)
    }
}
