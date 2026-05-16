package com.habitos.domain.usecase

import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.Habit
import com.habitos.domain.model.HabitCompletion
import com.habitos.domain.repository.FakeCompletionRepository
import com.habitos.domain.repository.FakeHabitRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TodayAndHistoryUseCasesTest {
    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var completionRepository: FakeCompletionRepository
    private lateinit var getHabitStreakUseCase: GetHabitStreakUseCase
    private lateinit var getTodayHabitsUseCase: GetTodayHabitsUseCase
    private lateinit var getHabitHistoryUseCase: GetHabitHistoryUseCase
    private lateinit var fixedClock: FixedClock

    @BeforeTest
    fun setup() {
        habitRepository = FakeHabitRepository()
        completionRepository = FakeCompletionRepository()
        fixedClock = FixedClock(Instant.parse("2026-01-15T12:00:00Z"))

        getHabitStreakUseCase = GetHabitStreakUseCase(habitRepository, completionRepository, fixedClock)
        getTodayHabitsUseCase = GetTodayHabitsUseCase(habitRepository, completionRepository, getHabitStreakUseCase, fixedClock)
        getHabitHistoryUseCase = GetHabitHistoryUseCase(completionRepository)
    }

    @AfterTest
    fun tearDown() {
        habitRepository.clear()
        completionRepository.clear()
    }

    @Test
    fun `GetTodayHabits returns mapped habits with correct status`() {
        val habit1 = Habit("1", "Read", null, "#000", FrequencyType.Daily, LocalDate(2026, 1, 1), null, Instant.fromEpochSeconds(0))
        val habit2 = Habit("2", "Run", null, "#000", FrequencyType.Daily, LocalDate(2026, 1, 1), null, Instant.fromEpochSeconds(0))
        val archived = Habit("3", "Archived", null, "#000", FrequencyType.Daily, LocalDate(2026, 1, 1), LocalDate(2026, 1, 10), Instant.fromEpochSeconds(0))

        habitRepository.save(habit1)
        habitRepository.save(habit2)
        habitRepository.save(archived)

        // Complete only habit1 today (Jan 15th 2026)
        val today = LocalDate(2026, 1, 15)
        completionRepository.save(HabitCompletion("c1", "1", today, Instant.fromEpochSeconds(0)))

        val results = getTodayHabitsUseCase()

        assertEquals(2, results.size)

        val result1 = results.find { it.habit.id == "1" }!!
        assertTrue(result1.isCompletedToday)
        assertEquals(1, result1.currentStreak) // Only today is completed

        val result2 = results.find { it.habit.id == "2" }!!
        assertFalse(result2.isCompletedToday)
        assertEquals(0, result2.currentStreak)
    }

    @Test
    fun `GetHabitHistoryUseCase returns dates inside the given month and year`() {
        val habitId = "habit1"
        completionRepository.save(HabitCompletion("c1", habitId, LocalDate(2025, 12, 31), Instant.fromEpochSeconds(0)))
        completionRepository.save(HabitCompletion("c2", habitId, LocalDate(2026, 1, 1), Instant.fromEpochSeconds(0)))
        completionRepository.save(HabitCompletion("c3", habitId, LocalDate(2026, 1, 15), Instant.fromEpochSeconds(0)))
        completionRepository.save(HabitCompletion("c4", habitId, LocalDate(2026, 1, 31), Instant.fromEpochSeconds(0)))
        completionRepository.save(HabitCompletion("c5", habitId, LocalDate(2026, 2, 1), Instant.fromEpochSeconds(0)))

        val history = getHabitHistoryUseCase(habitId, 2026, 1)

        assertEquals(3, history.size)
        assertTrue(history.contains(LocalDate(2026, 1, 1)))
        assertTrue(history.contains(LocalDate(2026, 1, 15)))
        assertTrue(history.contains(LocalDate(2026, 1, 31)))
    }

    @Test
    fun `GetHabitHistoryUseCase returns empty list if no completions in month`() {
        val history = getHabitHistoryUseCase("habit1", 2026, 3)
        assertTrue(history.isEmpty())
    }
}
