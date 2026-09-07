package com.fahim.learncoroutinesbytutorials.ui.errorhandling.supervisor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.learncoroutinesbytutorials.ui.base.ViewModelFactory
import com.fahim.learncoroutinesbytutorials.ui.components.UserListScreen
import com.fahim.learncoroutinesbytutorials.ui.components.toUserRow
import com.fahim.learncoroutinesbytutorials.ui.navigation.Screen
import com.fahim.learncoroutinesbytutorials.util.mapSuccess

@Composable
fun IgnoreErrorAndContinueScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit,
    viewModel: IgnoreErrorAndContinueViewModel = viewModel(factory = factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    UserListScreen(
        title = Screen.IgnoreErrorAndContinue.title,
        state = state.mapSuccess { users -> users.map { it.toUserRow() } },
        onBack = onBack
    )
}
