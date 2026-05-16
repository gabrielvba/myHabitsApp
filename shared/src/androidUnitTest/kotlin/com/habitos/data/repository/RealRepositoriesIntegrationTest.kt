package com.habitos.data.repository

import com.habitos.data.db.HabitosDatabase
import com.habitos.data.db.createInMemoryDriver
import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.Habit
import com.habitos.domain.model.HabitCompletion
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class RealRepositoriesIntegrationTest {

    private lateinit var database: HabitosDatabase
    private lateinit var habitRepository: HabitRepositoryImpl
    private lateinit var completionRepository: CompletionRepositoryImpl

    @BeforeTest
    fun setup() {
        val driver = createInMemoryDriver()
        database = HabitosDatabase(driver)
        habitRepository = HabitRepositoryImpl(database)
        completionRepository = CompletionRepositoryImpl(database)
    }

    @AfterTest
    fun tearDown() {
        // Driver in memory clears automatically on close or we just recreate it each test
    }

    @Test
    fun `save and findById habit using SQLDelight`() {
        val habit = Habit(
            id = "uuid-1",
            name = "Study KMP",
            emoji = "💻",
            color = "#ff0000",
            frequency = FrequencyType.Weekly(3),
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = null,
            updatedAt = Instant.fromEpochSeconds(12345678)
        )

        habitRepository.save(habit)

        val retrieved = habitRepository.findById("uuid-1")
        assertNotNull(retrieved)
        assertEquals("Study KMP", retrieved.name)
        assertEquals(3, (retrieved.frequency as FrequencyType.Weekly).timesPerWeek)
    }

    @Test
    fun `save completion with UNIQUE constraint throws exception on duplicate`() {
        val habit = Habit(
            id = "uuid-2",
            name = "Study KMP",
            emoji = "💻",
            color = "#ff0000",
            frequency = FrequencyType.Daily,
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = null,
            updatedAt = Instant.fromEpochSeconds(0)
        )
        habitRepository.save(habit)

        val completion = HabitCompletion(
            id = "comp-1",
            habitId = "uuid-2",
            date = LocalDate(2026, 1, 15),
            completedAt = Instant.fromEpochSeconds(0)
        )

        // First save should pass
        completionRepository.save(completion)

        val completionDuplicateDate = HabitCompletion(
            id = "comp-2",
            habitId = "uuid-2",
            date = LocalDate(2026, 1, 15),
            completedAt = Instant.fromEpochSeconds(100)
        )

        // Second save should fail at SQLite level due to UNIQUE(habit_id, date) constraint
        assertFails {
            completionRepository.save(completionDuplicateDate)
        }
    }
}
