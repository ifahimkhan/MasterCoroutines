package com.fahim.learncoroutinesbytutorials.ui.errorhandling.trycatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Error handling with try-catch.
 *
 * A suspend function that throws behaves like a normal function that throws:
 * wrap the call in try-catch inside the coroutine. The endpoint used here
 * always fails, so the Error state is expected.
 */
class TryCatchViewModel(
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
                val usersFromApi = apiHelper.getUsersWithError()
                _uiState.value = UiState.Success(usersFromApi)
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }
}
