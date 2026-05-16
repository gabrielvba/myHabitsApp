package com.habitos.androidApp.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    viewModel: CreateHabitViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Hábito") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("<") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.emoji,
                onValueChange = viewModel::updateEmoji,
                label = { Text("Emoji") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Frequência:")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = !state.isWeekly,
                    onClick = { viewModel.updateFrequency(false) }
                )
                Text("Diária")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = state.isWeekly,
                    onClick = { viewModel.updateFrequency(true) }
                )
                Text("Semanal")
            }

            if (state.isWeekly) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Vezes por semana:")
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = state.timesPerWeek.toFloat(),
                        onValueChange = { viewModel.updateTimesPerWeek(it.toInt()) },
                        valueRange = 1f..7f,
                        steps = 5,
                        modifier = Modifier.weight(1f)
                    )
                    Text("\${state.timesPerWeek}x")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            state.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}
