package com.fahim.learncoroutinesbytutorials.ui.retrofit.single

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
 * Single network call.
 *
 * `viewModelScope.launch` starts a coroutine on Dispatchers.Main. The suspend
 * call `apiHelper.getUsers()` does not block the main thread: the coroutine is
 * suspended until Retrofit delivers the result, then resumes on Main.
 */
class SingleNetworkCallViewModel(
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
                val usersFromApi = apiHelper.getUsers()
                _uiState.value = UiState.Success(usersFromApi)
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.toString())
            }
        }
    }
}
