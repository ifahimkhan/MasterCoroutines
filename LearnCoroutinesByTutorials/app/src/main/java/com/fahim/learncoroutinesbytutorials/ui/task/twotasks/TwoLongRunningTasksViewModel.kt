package com.fahim.learncoroutinesbytutorials.ui.task.twotasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Two long-running tasks in parallel.
 *
 * Each task takes 2 seconds. Run with `async` they overlap, so the combined
 * result arrives after ~2 seconds instead of ~4.
 */
class TwoLongRunningTasksViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Success("Idle"))
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    fun startLongRunningTask() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val resultOneDeferred = async { doLongRunningTaskOne() }
                val resultTwoDeferred = async { doLongRunningTaskTwo() }
                val combinedResult = resultOneDeferred.await() + resultTwoDeferred.await()
                _uiState.value = UiState.Success("Task Completed : $combinedResult")
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }

    private suspend fun doLongRunningTaskOne(): Int {
        return withContext(defaultDispatcher) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(TASK_DURATION_MS)
            return@withContext TASK_RESULT
        }
    }

    private suspend fun doLongRunningTaskTwo(): Int {
        return withContext(defaultDispatcher) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(TASK_DURATION_MS)
            return@withContext TASK_RESULT
        }
    }

    companion object {
        const val TASK_DURATION_MS = 2000L
        const val TASK_RESULT = 10
    }
}
