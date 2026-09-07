package com.fahim.learncoroutinesbytutorials.ui.retrofit.parallel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Two network calls in parallel.
 *
 * `async` starts each call immediately and returns a `Deferred`. Both requests
 * are in flight at the same time; `await()` suspends until each result is ready.
 * Any failure propagates to the parent and is caught by the
 * [CoroutineExceptionHandler] installed on `launch`.
 */
class ParallelNetworkCallsViewModel(
    private val apiHelper: ApiHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<ApiUser>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ApiUser>>> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        _uiState.value = UiState.Error("exception handler: $e")
    }

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = UiState.Loading

            val usersFromApiDeferred = async { apiHelper.getUsers() }
            val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }

            val usersFromApi = usersFromApiDeferred.await()
            val moreUsersFromApi = moreUsersFromApiDeferred.await()

            _uiState.value = UiState.Success(usersFromApi + moreUsersFromApi)
        }
    }
}
