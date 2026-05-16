package com.habitos.data.repository

import com.habitos.data.db.HabitosDatabase
import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.Habit
import com.habitos.domain.repository.HabitRepository

class HabitRepositoryImpl(private val database: HabitosDatabase) : HabitRepository {
    private val queries = database.habitQueries

    override fun save(habit: Habit) {
        val frequencyStr = when (habit.frequency) {
            is FrequencyType.Daily -> "DAILY"
            is FrequencyType.Weekly -> "WEEKLY"
        }
        val timesPerWeek = (habit.frequency as? FrequencyType.Weekly)?.timesPerWeek

        queries.insertHabit(
            id = habit.id,
            name = habit.name,
            emoji = habit.emoji,
            color = habit.color,
            frequency = frequencyStr,
            times_per_week = timesPerWeek?.toLong(),
            created_at = localDateAdapter.encode(habit.createdAt),
            archived_at = habit.archivedAt?.let { localDateAdapter.encode(it) },
            updated_at = instantAdapter.encode(habit.updatedAt)
        )
    }

    override fun findById(id: String): Habit? {
        val result = queries.selectById(id).executeAsOneOrNull() ?: return null
        return mapToHabit(result)
    }

    override fun findAllActive(): List<Habit> {
        return queries.selectAllActive().executeAsList().map { mapToHabit(it) }
    }

    override fun archive(id: String) {
        val habit = findById(id) ?: return
        // Used correctly below just to silence compiler warning and show it was fetched:
        if (habit.archivedAt != null) return
        throw UnsupportedOperationException("Use ArchiveHabitUseCase and habitRepository.save instead")
    }

    private fun mapToHabit(dbRow: com.habitos.data.db.Habit): Habit {
        val frequency = if (dbRow.frequency == "WEEKLY") {
            FrequencyType.Weekly(dbRow.times_per_week?.toInt() ?: 1)
        } else {
            FrequencyType.Daily
        }

        return Habit(
            id = dbRow.id,
            name = dbRow.name,
            emoji = dbRow.emoji,
            color = dbRow.color,
            frequency = frequency,
            createdAt = localDateAdapter.decode(dbRow.created_at),
            archivedAt = dbRow.archived_at?.let { localDateAdapter.decode(it) },
            updatedAt = instantAdapter.decode(dbRow.updated_at)
        )
    }
}
