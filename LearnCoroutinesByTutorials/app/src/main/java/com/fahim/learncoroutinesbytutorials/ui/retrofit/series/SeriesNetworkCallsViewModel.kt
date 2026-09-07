package com.fahim.learncoroutinesbytutorials.ui.retrofit.series

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
 * Two network calls in series.
 *
 * Suspend functions called one after another run sequentially: the second
 * call starts only after the first has returned. Use this when call #2
 * depends on the result of call #1.
 */
class SeriesNetworkCallsViewModel(
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
                val moreUsersFromApi = apiHelper.getMoreUsers()
                val allUsersFromApi = usersFromApi + moreUsersFromApi
                _uiState.value = UiState.Success(allUsersFromApi)
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }
}
