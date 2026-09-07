package com.fahim.learncoroutinesbytutorials.ui.errorhandling.supervisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Ignore one child's error and continue with the others.
 *
 * Inside `coroutineScope`, a failing child cancels its siblings and the parent.
 * Inside `supervisorScope`, a failing child affects only itself, so we can
 * `try { await() }` each Deferred independently and keep the successful ones.
 */
class IgnoreErrorAndContinueViewModel(
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
            // supervisorScope is needed so that we can ignore an error and continue.
            // Two child jobs run in parallel under a supervisor; if one fails,
            // we continue with the other.
            supervisorScope {
                val usersFromApiDeferred = async { apiHelper.getUsersWithError() }
                val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }

                val usersFromApi = try {
                    usersFromApiDeferred.await()
                } catch (e: CancellationException) {
                    // Never swallow cancellation: rethrow so structured concurrency keeps working
                    throw e
                } catch (e: Exception) {
                    emptyList()
                }

                val moreUsersFromApi = try {
                    moreUsersFromApiDeferred.await()
                } catch (e: CancellationException) {
                    // Never swallow cancellation: rethrow so structured concurrency keeps working
                    throw e
                } catch (e: Exception) {
                    emptyList()
                }

                _uiState.value = UiState.Success(usersFromApi + moreUsersFromApi)
            }
        }
    }
}
