package com.habitos.data.repository

import com.habitos.data.db.HabitosDatabase
import com.habitos.domain.model.HabitCompletion
import com.habitos.domain.repository.CompletionRepository
import kotlinx.datetime.LocalDate

class CompletionRepositoryImpl(private val database: HabitosDatabase) : CompletionRepository {
    private val queries = database.habitCompletionQueries

    override fun save(completion: HabitCompletion) {
        try {
            queries.insertCompletion(
                id = completion.id,
                habit_id = completion.habitId,
                date = localDateAdapter.encode(completion.date),
                completed_at = instantAdapter.encode(completion.completedAt)
            )
        } catch (e: Exception) {
            // In a real app we would catch SQLiteException and rethrow a domain exception if it's a constraint fail.
            // For now, if UNIQUE constraint fails, it throws an exception (which handles RN-01 via DB level).
            throw e
        }
    }

    override fun delete(habitId: String, date: LocalDate) {
        queries.deleteCompletion(
            habit_id = habitId,
            date = localDateAdapter.encode(date)
        )
    }

    override fun findByHabitId(habitId: String): List<HabitCompletion> {
        return queries.selectByHabitId(habitId).executeAsList().map { mapToCompletion(it) }
    }

    override fun findByHabitIdAndDate(habitId: String, date: LocalDate): HabitCompletion? {
        val result = queries.selectByHabitIdAndDate(
            habit_id = habitId,
            date = localDateAdapter.encode(date)
        ).executeAsOneOrNull() ?: return null

        return mapToCompletion(result)
    }

    override fun findByHabitIdBetween(habitId: String, startDate: LocalDate, endDate: LocalDate): List<HabitCompletion> {
        return queries.selectByHabitIdBetween(
            habit_id = habitId,
            date = localDateAdapter.encode(startDate),
            date_ = localDateAdapter.encode(endDate) // SQLDelight generated name for the second bound
        ).executeAsList().map { mapToCompletion(it) }
    }

    private fun mapToCompletion(dbRow: com.habitos.data.db.HabitCompletion): HabitCompletion {
        return HabitCompletion(
            id = dbRow.id,
            habitId = dbRow.habit_id,
            date = localDateAdapter.decode(dbRow.date),
            completedAt = instantAdapter.decode(dbRow.completed_at)
        )
    }
}
