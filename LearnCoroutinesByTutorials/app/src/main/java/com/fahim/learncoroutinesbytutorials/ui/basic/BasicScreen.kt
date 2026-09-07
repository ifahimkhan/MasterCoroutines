package com.fahim.learncoroutinesbytutorials.ui.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.learncoroutinesbytutorials.ui.components.ExampleScaffold
import com.fahim.learncoroutinesbytutorials.ui.components.LogConsole

@Composable
fun BasicScreen(
    onBack: () -> Unit,
    viewModel: BasicViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    ExampleScaffold(title = "Basics", onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LogConsole(
                logs = logs,
                onClear = viewModel::clearLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(viewModel.examples, key = { it.title }) { example ->
                    BasicExampleCard(example)
                }
            }
        }
    }
}

@Composable
private fun BasicExampleCard(example: BasicExample) {
    Card(
        onClick = example.run,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = example.title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = example.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
