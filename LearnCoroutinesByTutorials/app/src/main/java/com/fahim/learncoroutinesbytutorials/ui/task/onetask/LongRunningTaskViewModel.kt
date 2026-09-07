package com.fahim.learncoroutinesbytutorials.ui.task.onetask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * One long-running task on a background thread.
 *
 * `withContext(Dispatchers.Default)` switches the coroutine to a CPU-bound
 * thread pool for the work, then switches back to Main when done. The
 * dispatcher is injectable so unit tests can swap in a test dispatcher.
 */
class LongRunningTaskViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Success("Idle"))
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    fun startLongRunningTask() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                doLongRunningTask()
                _uiState.value = UiState.Success("Task Completed")
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }

    private suspend fun doLongRunningTask() {
        withContext(defaultDispatcher) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(TASK_DURATION_MS)
        }
    }

    companion object {
        const val TASK_DURATION_MS = 5000L
    }
}
