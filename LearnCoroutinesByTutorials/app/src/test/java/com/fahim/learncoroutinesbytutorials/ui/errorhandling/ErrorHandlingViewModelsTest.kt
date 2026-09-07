package com.fahim.learncoroutinesbytutorials.ui.errorhandling

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.exceptionhandler.ExceptionHandlerViewModel
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.supervisor.IgnoreErrorAndContinueViewModel
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.trycatch.TryCatchViewModel
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
class ErrorHandlingViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun tryCatch_givenFailingEndpoint_shouldReturnError() = runTest {
        val apiHelper = FakeApiHelper(usersWithErrorError = RuntimeException("boom"))

        val viewModel = TryCatchViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(1, apiHelper.getUsersWithErrorCalls)
        assertEquals(UiState.Error("Something Went Wrong"), viewModel.uiState.value)
    }

    @Test
    fun tryCatch_givenEndpointSucceeds_shouldReturnSuccess() = runTest {
        val apiHelper = FakeApiHelper(usersWithErrorError = null)

        val viewModel = TryCatchViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Success(emptyList<Any>()), viewModel.uiState.value)
    }

    @Test
    fun exceptionHandler_givenFailingEndpoint_shouldReportViaHandler() = runTest {
        val error = RuntimeException("boom")
        val apiHelper = FakeApiHelper(usersWithErrorError = error)

        val viewModel = ExceptionHandlerViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Error("exception handler: $error"), viewModel.uiState.value)
    }

    @Test
    fun supervisor_givenOneChildFails_shouldStillReturnOtherChildResult() = runTest {
        val apiHelper = FakeApiHelper(
            moreUsers = TestData.moreUsers,
            usersWithErrorError = RuntimeException("boom")
        )

        val viewModel = IgnoreErrorAndContinueViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(1, apiHelper.getUsersWithErrorCalls)
        assertEquals(1, apiHelper.getMoreUsersCalls)
        assertEquals(UiState.Success(TestData.moreUsers), viewModel.uiState.value)
    }

    @Test
    fun supervisor_givenBothChildrenFail_shouldReturnEmptySuccess() = runTest {
        val apiHelper = FakeApiHelper(
            moreUsersError = RuntimeException("boom 2"),
            usersWithErrorError = RuntimeException("boom 1")
        )

        val viewModel = IgnoreErrorAndContinueViewModel(apiHelper)
        advanceUntilIdle()

        assertEquals(UiState.Success(emptyList<Any>()), viewModel.uiState.value)
    }
}
