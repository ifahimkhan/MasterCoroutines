package com.fahim.learncoroutinesbytutorials.ui.task.twotasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.learncoroutinesbytutorials.ui.base.ViewModelFactory
import com.fahim.learncoroutinesbytutorials.ui.components.TaskScreen
import com.fahim.learncoroutinesbytutorials.ui.navigation.Screen

@Composable
fun TwoLongRunningTasksScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit,
    viewModel: TwoLongRunningTasksViewModel = viewModel(factory = factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskScreen(
        title = Screen.TwoLongRunningTasks.title,
        description = "Runs two 2 second tasks with async. They overlap, " +
            "so the combined result arrives after about 2 seconds, not 4.",
        state = state,
        onStart = viewModel::startLongRunningTask,
        onBack = onBack
    )
}
