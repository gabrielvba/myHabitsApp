package com.habitos.domain.usecase

import com.habitos.domain.model.HabitCompletion
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StreakCalculatorsTest {

    private fun mockCompletion(date: LocalDate): HabitCompletion {
        return HabitCompletion("id", "habit", date, Instant.fromEpochSeconds(0))
    }

    // --- DAILY STREAK TESTS ---

    @Test
    fun `Daily without completions returns 0 current and 0 best`() {
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 10)

        val (current, best) = DailyStreakCalculator.calculate(emptyList(), today, createdAt)

        assertEquals(0, current)
        assertEquals(0, best)
    }

    @Test
    fun `Daily with 5 consecutive days returns 5 current`() {
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 11)
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 11)),
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 13)),
            mockCompletion(LocalDate(2026, 1, 14)),
            mockCompletion(LocalDate(2026, 1, 15))
        )

        val (current, best) = DailyStreakCalculator.calculate(completions, today, createdAt)

        assertEquals(5, current)
        assertEquals(5, best)
    }

    @Test
    fun `Daily with gap yesterday breaks current streak`() {
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 11)
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 11)),
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 13)),
            // missing 14th
            mockCompletion(LocalDate(2026, 1, 15))
        )

        val (current, best) = DailyStreakCalculator.calculate(completions, today, createdAt)

        assertEquals(1, current) // Only today is counted in current
        assertEquals(3, best)    // 11, 12, 13 is the best
    }

    @Test
    fun `Daily streak 7 gap streak 3 returns current 3 and best 7`() {
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 1)
        val completions = listOf(
            // Streak 7
            mockCompletion(LocalDate(2026, 1, 1)),
            mockCompletion(LocalDate(2026, 1, 2)),
            mockCompletion(LocalDate(2026, 1, 3)),
            mockCompletion(LocalDate(2026, 1, 4)),
            mockCompletion(LocalDate(2026, 1, 5)),
            mockCompletion(LocalDate(2026, 1, 6)),
            mockCompletion(LocalDate(2026, 1, 7)),
            // gap 8 to 11
            // Streak 3 ending yesterday
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 13)),
            mockCompletion(LocalDate(2026, 1, 14))
            // 15th (today) missing, but current shouldn't break entirely if today is not yet done
        )

        val (current, best) = DailyStreakCalculator.calculate(completions, today, createdAt)

        // Since today is not completed but yesterday was, current should be 3
        assertEquals(3, current)
        assertEquals(7, best)
    }

    @Test
    fun `Daily gap today but consecutive yesterday keeps current alive`() {
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 13)
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 13)),
            mockCompletion(LocalDate(2026, 1, 14))
        )

        val (current, best) = DailyStreakCalculator.calculate(completions, today, createdAt)

        // 15th missing, but user still has time today
        assertEquals(2, current)
        assertEquals(2, best)
    }

    // --- WEEKLY STREAK TESTS ---

    @Test
    fun `Weekly 3x with 3 completions in week returns 1 current`() {
        // Jan 15th 2026 is Thursday. Week is Jan 12th (Mon) to Jan 18th (Sun)
        val today = LocalDate(2026, 1, 15)
        val createdAt = LocalDate(2026, 1, 12)
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 12)), // Mon
            mockCompletion(LocalDate(2026, 1, 14)), // Wed
            mockCompletion(LocalDate(2026, 1, 15))  // Thu
        )

        val (current, best) = WeeklyStreakCalculator.calculate(completions, today, createdAt, 3)

        assertEquals(1, current)
        assertEquals(1, best)
    }

    @Test
    fun `Weekly 3x with 2 completions in week returns 0 current`() {
        // In the middle of week, missing target doesn't penalize current. Wait, the test case says:
        // "Hábito semanal 3x: semana com 2 completions = quebrada; current = 0"
        // Let's assume the week is IN THE PAST
        val today = LocalDate(2026, 1, 22) // Next week
        val createdAt = LocalDate(2026, 1, 12) // Previous week start

        // Previous week had 2, current week 0
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 14))
        )

        val (current, best) = WeeklyStreakCalculator.calculate(completions, today, createdAt, 3)

        assertEquals(0, current)
        assertEquals(0, best) // Added assertion for best
    }

    @Test
    fun `Weekly 3x with 3 weeks fulfilled returns current 3`() {
        val today = LocalDate(2026, 1, 29) // Week 3
        val createdAt = LocalDate(2026, 1, 12) // Week 1

        val completions = listOf(
            // Week 1
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 14)),
            mockCompletion(LocalDate(2026, 1, 15)),
            // Week 2
            mockCompletion(LocalDate(2026, 1, 19)),
            mockCompletion(LocalDate(2026, 1, 20)),
            mockCompletion(LocalDate(2026, 1, 21)),
            // Week 3
            mockCompletion(LocalDate(2026, 1, 26)),
            mockCompletion(LocalDate(2026, 1, 27)),
            mockCompletion(LocalDate(2026, 1, 28))
        )

        val (current, best) = WeeklyStreakCalculator.calculate(completions, today, createdAt, 3)

        assertEquals(3, current)
        assertEquals(3, best)
    }

    @Test
    fun `Weekly 3x start monday ISO test`() {
        val today = LocalDate(2026, 1, 18) // Sunday
        val createdAt = LocalDate(2026, 1, 12) // Monday

        // Completions exactly on Monday, Friday and Sunday
        val completions = listOf(
            mockCompletion(LocalDate(2026, 1, 12)),
            mockCompletion(LocalDate(2026, 1, 16)),
            mockCompletion(LocalDate(2026, 1, 18))
        )

        val (current, _) = WeeklyStreakCalculator.calculate(completions, today, createdAt, 3)
        assertEquals(1, current)
    }
}
