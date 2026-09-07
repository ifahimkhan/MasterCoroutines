package com.fahim.learncoroutinesbytutorials.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fahim.learncoroutinesbytutorials.ui.base.UiState

/**
 * Generic "list of users" example screen. Every network / DB example renders
 * through this so the ViewModels stay the focus.
 */
@Composable
fun UserListScreen(
    title: String,
    state: UiState<List<UserRow>>,
    onBack: () -> Unit
) {
    ExampleScaffold(title = title, onBack = onBack) { innerPadding ->
        UiStateContent(
            state = state,
            modifier = Modifier.padding(innerPadding)
        ) { users ->
            UserList(users = users)
        }
    }
}
