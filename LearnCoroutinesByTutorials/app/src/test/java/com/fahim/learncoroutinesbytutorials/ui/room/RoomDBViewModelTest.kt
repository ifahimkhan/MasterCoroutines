package com.fahim.learncoroutinesbytutorials.ui.room

import com.fahim.learncoroutinesbytutorials.data.local.entity.User
import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import com.fahim.learncoroutinesbytutorials.utils.FakeApiHelper
import com.fahim.learncoroutinesbytutorials.utils.FakeDatabaseHelper
import com.fahim.learncoroutinesbytutorials.utils.MainDispatcherRule
import com.fahim.learncoroutinesbytutorials.utils.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomDBViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cachedUsers = listOf(
        User(id = 9, name = "Cached", email = "cached@example.com", avatar = "z.png")
    )

    @Test
    fun givenDbHasUsers_whenFetch_shouldReturnDbUsersWithoutApiCall() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users)
        val dbHelper = FakeDatabaseHelper(initialUsers = cachedUsers)

        val viewModel = RoomDBViewModel(apiHelper, dbHelper)
        advanceUntilIdle()

        assertEquals(0, apiHelper.getUsersCalls)
        assertEquals(UiState.Success(cachedUsers), viewModel.uiState.value)
    }

    @Test
    fun givenDbEmpty_whenFetch_shouldCallApiInsertAndReturn() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users)
        val dbHelper = FakeDatabaseHelper()

        val viewModel = RoomDBViewModel(apiHelper, dbHelper)
        advanceUntilIdle()

        val expected = TestData.users.map { User(it.id, it.name, it.email, it.avatar) }
        assertEquals(1, apiHelper.getUsersCalls)
        assertEquals(expected, dbHelper.stored)
        assertEquals(UiState.Success(expected), viewModel.uiState.value)
    }

    @Test
    fun givenDbThrows_whenFetch_shouldReturnError() = runTest {
        val apiHelper = FakeApiHelper(users = TestData.users)
        val dbHelper = FakeDatabaseHelper(error = IllegalStateException("db closed"))

        val viewModel = RoomDBViewModel(apiHelper, dbHelper)
        advanceUntilIdle()

        assertEquals(UiState.Error("Something Went Wrong"), viewModel.uiState.value)
    }
}
