package com.habitos.domain.usecase

import com.habitos.domain.model.CannotCompleteInPastException
import com.habitos.domain.model.CompletionNotFoundException
import com.habitos.domain.model.HabitAlreadyCompletedException
import com.habitos.domain.repository.FakeCompletionRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}

class CompletionUseCasesTest {

    private lateinit var completionRepository: FakeCompletionRepository
    private lateinit var completeHabitUseCase: CompleteHabitUseCase
    private lateinit var uncompleteHabitUseCase: UncompleteHabitUseCase
    private lateinit var fixedClock: FixedClock

    @BeforeTest
    fun setup() {
        completionRepository = FakeCompletionRepository()
        // Instant from "2026-01-15T12:00:00Z"
        fixedClock = FixedClock(Instant.parse("2026-01-15T12:00:00Z"))
        completeHabitUseCase = CompleteHabitUseCase(completionRepository, fixedClock)
        uncompleteHabitUseCase = UncompleteHabitUseCase(completionRepository)
    }

    @AfterTest
    fun tearDown() {
        completionRepository.clear()
    }

    @Test
    fun `CompleteHabitUseCase creates completion if date is today`() {
        val today = LocalDate(2026, 1, 15)
        val habitId = "test-habit-id"

        val completion = completeHabitUseCase(habitId, today)

        assertNotNull(completion.id)
        assertEquals(habitId, completion.habitId)
        assertEquals(today, completion.date)
        assertEquals(1, completionRepository.findByHabitId(habitId).size)
    }

    @Test
    fun `CompleteHabitUseCase throws CannotCompleteInPastException if date is not today`() {
        val habitId = "test-habit-id"
        val pastDate = LocalDate(2026, 1, 14)

        assertFailsWith<CannotCompleteInPastException> {
            completeHabitUseCase(habitId, pastDate)
        }
    }

    @Test
    fun `CompleteHabitUseCase throws HabitAlreadyCompletedException if completed twice today`() {
        val today = LocalDate(2026, 1, 15)
        val habitId = "test-habit-id"

        completeHabitUseCase(habitId, today)

        assertFailsWith<HabitAlreadyCompletedException> {
            completeHabitUseCase(habitId, today)
        }
    }

    @Test
    fun `UncompleteHabitUseCase removes completion`() {
        val today = LocalDate(2026, 1, 15)
        val habitId = "test-habit-id"

        completeHabitUseCase(habitId, today)
        assertEquals(1, completionRepository.findByHabitId(habitId).size)

        uncompleteHabitUseCase(habitId, today)
        assertEquals(0, completionRepository.findByHabitId(habitId).size)
    }

    @Test
    fun `UncompleteHabitUseCase throws CompletionNotFoundException if not found`() {
        val today = LocalDate(2026, 1, 15)
        val habitId = "test-habit-id"

        assertFailsWith<CompletionNotFoundException> {
            uncompleteHabitUseCase(habitId, today)
        }
    }
}
