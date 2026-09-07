package com.fahim.learncoroutinesbytutorials.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.local.DatabaseHelper
import com.fahim.learncoroutinesbytutorials.data.local.entity.User
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Room database with coroutines.
 *
 * Reads from the DB first (suspend DAO call). If empty, fetches from the API,
 * inserts into the DB and shows the result. All calls are suspend functions
 * chained inside a single coroutine, so the code reads top-to-bottom.
 */
class RoomDBViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.asStateFlow()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val usersFromDb = dbHelper.getUsers()
                if (usersFromDb.isEmpty()) {
                    val usersFromApi = apiHelper.getUsers()
                    val usersToInsertInDB = usersFromApi.map { apiUser ->
                        User(
                            id = apiUser.id,
                            name = apiUser.name,
                            email = apiUser.email,
                            avatar = apiUser.avatar
                        )
                    }
                    dbHelper.insertAll(usersToInsertInDB)
                    _uiState.value = UiState.Success(usersToInsertInDB)
                } else {
                    _uiState.value = UiState.Success(usersFromDb)
                }
            } catch (e: CancellationException) {
                // Never swallow cancellation: rethrow so structured concurrency keeps working
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Something Went Wrong")
            }
        }
    }
}
