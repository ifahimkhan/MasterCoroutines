package com.fahim.learncoroutinesbytutorials.ui.task

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.ui.task.onetask.LongRunningTaskViewModel
import com.fahim.learncoroutinesbytutorials.ui.task.twotasks.TwoLongRunningTasksViewModel
import com.fahim.learncoroutinesbytutorials.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LongRunningTaskViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun singleTask_completesAfterItsDuration() = runTest {
        val viewModel = LongRunningTaskViewModel(defaultDispatcher = mainDispatcherRule.testDispatcher)
        assertEquals(UiState.Success("Idle"), viewModel.uiState.value)

        viewModel.startLongRunningTask()
        advanceTimeBy(1)
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceTimeBy(LongRunningTaskViewModel.TASK_DURATION_MS - 1)
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceTimeBy(1)
        assertEquals(UiState.Success("Task Completed"), viewModel.uiState.value)
    }

    @Test
    fun twoTasks_runInParallel_completeAfterSingleDurationNotDouble() = runTest {
        val viewModel = TwoLongRunningTasksViewModel(defaultDispatcher = mainDispatcherRule.testDispatcher)

        viewModel.startLongRunningTask()
        advanceTimeBy(1)
        assertEquals(UiState.Loading, viewModel.uiState.value)

        // After a single task duration both async tasks are done.
        advanceTimeBy(TwoLongRunningTasksViewModel.TASK_DURATION_MS)
        val expectedSum = TwoLongRunningTasksViewModel.TASK_RESULT * 2
        assertEquals(UiState.Success("Task Completed : $expectedSum"), viewModel.uiState.value)
    }
}
