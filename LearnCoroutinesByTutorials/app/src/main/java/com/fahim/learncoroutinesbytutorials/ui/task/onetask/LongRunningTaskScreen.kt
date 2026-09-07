package com.fahim.learncoroutinesbytutorials.ui.task.onetask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.learncoroutinesbytutorials.ui.base.ViewModelFactory
import com.fahim.learncoroutinesbytutorials.ui.components.TaskScreen
import com.fahim.learncoroutinesbytutorials.ui.navigation.Screen

@Composable
fun LongRunningTaskScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit,
    viewModel: LongRunningTaskViewModel = viewModel(factory = factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskScreen(
        title = Screen.LongRunningTask.title,
        description = "Runs a 5 second task with withContext(Dispatchers.Default). " +
            "The UI stays responsive while it runs.",
        state = state,
        onStart = viewModel::startLongRunningTask,
        onBack = onBack
    )
}
