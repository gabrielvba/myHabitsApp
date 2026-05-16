package com.habitos.androidApp.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    viewModel: HabitDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Hábito") },
                navigationIcon = {
                    Button(onClick = onBack) { Text("<") }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                Text("🔥 Streak Atual: \${state.streak?.current ?: 0}", style = MaterialTheme.typography.titleLarge)
                Text("🏆 Melhor Streak: \${state.streak?.best ?: 0}", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(24.dp))

                Text("Mês \${state.currentMonth} / \${state.currentYear}", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                // Extremely simple calendar logic: 1 to 31
                val daysInMonth = (1..31).toList() // Approximated for mockup
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(daysInMonth) { day ->
                        // If month has less days, it would fail in real LocalDate parsing, just mockup for UI proof:
                        val isCompleted = try {
                            val date = LocalDate(state.currentYear, state.currentMonth, day)
                            state.historyDates.contains(date)
                        } catch (e: Exception) { false }

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .background(if (isCompleted) Color(0xFF4CAF50) else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(day.toString(), color = if (isCompleted) Color.White else Color.Black)
                        }
                    }
                }
            }
        }
    }
}
