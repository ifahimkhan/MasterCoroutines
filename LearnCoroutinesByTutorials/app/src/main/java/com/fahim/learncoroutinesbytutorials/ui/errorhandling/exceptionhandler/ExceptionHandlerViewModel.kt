package com.fahim.learncoroutinesbytutorials.ui.errorhandling.exceptionhandler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Error handling with [CoroutineExceptionHandler].
 *
 * Instead of try-catch, the handler is added to the coroutine context of
 * `launch`. Any uncaught exception in that coroutine (or its children) is
 * delivered to the handler. Note: it only works on root `launch` coroutines,
 * not on `async`.
 */
class ExceptionHandlerViewModel(
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
            val usersFromApi = apiHelper.getUsersWithError()
            _uiState.value = UiState.Success(usersFromApi)
        }
    }
}
