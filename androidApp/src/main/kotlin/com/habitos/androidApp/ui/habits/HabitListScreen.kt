package com.habitos.androidApp.ui.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitos.domain.model.HabitWithStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel,
    onCreateClick: () -> Unit,
    onHabitClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Text("+")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.habits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum hábito ativo hoje. Crie um novo!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(items = state.habits, key = { it.habit.id }) { habitStatus ->
                    HabitItem(
                        habitStatus = habitStatus,
                        onToggle = { viewModel.toggleHabitCompletion(habitStatus.habit.id, habitStatus.isCompletedToday) },
                        onClick = { onHabitClick(habitStatus.habit.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitItem(
    habitStatus: HabitWithStatus,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = habitStatus.habit.emoji ?: "📝", modifier = Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = habitStatus.habit.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "🔥 Streak: \${habitStatus.currentStreak}", style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(
                checked = habitStatus.isCompletedToday,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
