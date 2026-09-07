package com.fahim.learncoroutinesbytutorials.ui.timeout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * Timeout.
 *
 * `withTimeout` cancels the block and throws [TimeoutCancellationException]
 * if it has not completed within the given time. The 100 ms budget here is
 * intentionally too small for a real network call, so you will see the error.
 */
class TimeoutViewModel(
    private val apiHelper: ApiHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<ApiUser>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ApiUser>>> = _uiState.asStateFlow()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                withTimeout(TIMEOUT_MS) {
                    val usersFromApi = apiHelper.getUsers()
                    _uiState.value = UiState.Success(usersFromApi)
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = UiState.Error("TimeoutCancellationException")
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }

    companion object {
        const val TIMEOUT_MS = 100L
    }
}
