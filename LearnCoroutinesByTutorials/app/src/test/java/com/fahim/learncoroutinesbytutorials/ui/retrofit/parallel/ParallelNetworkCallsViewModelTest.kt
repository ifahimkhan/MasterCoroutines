package com.fahim.learncoroutinesbytutorials.ui.retrofit.parallel

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.utils.FakeApiHelper
import com.fahim.learncoroutinesbytutorials.utils.MainDispatcherRule
import com.fahim.learncoroutinesbytutorials.utils.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ParallelNetworkCallsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenBothCallsSucceed_whenFetch_shouldCombine() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users, moreUsers = TestData.moreUsers)

        val viewModel = ParallelNetworkCallsViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Success(TestData.users + TestData.moreUsers), viewModel.uiState.value)
    }

    @Test
    fun givenEachCallTakes1s_whenFetch_shouldRunInParallelTaking1s() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            moreUsers = TestData.moreUsers,
            delayMs = 1000
        )

        val viewModel = ParallelNetworkCallsViewModel(apiHelper)

        advanceTimeBy(1100)
        assertEquals(UiState.Success(TestData.users + TestData.moreUsers), viewModel.uiState.value)
    }

    @Test
    fun givenOneCallFails_whenFetch_shouldReportViaExceptionHandler() = runTest {
        val apiHelper = FakeApiHelper(
            users = TestData.users,
            moreUsersError = RuntimeException("boom")
        )

        val viewModel = ParallelNetworkCallsViewModel(apiHelper)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("expected Error but was $state", state is UiState.Error)
        assertTrue((state as UiState.Error).message.startsWith("exception handler:"))
    }
}
