package com.habitos.domain.repository

import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.Habit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HabitRepositoryFakeTest {
    private lateinit var repository: FakeHabitRepository

    @BeforeTest
    fun setup() {
        repository = FakeHabitRepository()
    }

    @AfterTest
    fun tearDown() {
        repository.clear()
    }

    @Test
    fun `save inserts a habit and findById retrieves it`() {
        val habit = Habit(
            id = "test-uuid",
            name = "Read",
            emoji = "📚",
            color = "#000000",
            frequency = FrequencyType.Daily,
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = null,
            updatedAt = Instant.fromEpochSeconds(0)
        )

        repository.save(habit)

        val retrieved = repository.findById("test-uuid")
        assertNotNull(retrieved)
        assertEquals("Read", retrieved.name)
    }

    @Test
    fun `findAllActive excludes archived habits`() {
        val activeHabit = Habit(
            id = "1",
            name = "Active",
            emoji = null,
            color = "#000",
            frequency = FrequencyType.Daily,
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = null,
            updatedAt = Instant.fromEpochSeconds(0)
        )
        val archivedHabit = Habit(
            id = "2",
            name = "Archived",
            emoji = null,
            color = "#000",
            frequency = FrequencyType.Daily,
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = LocalDate(2026, 1, 2),
            updatedAt = Instant.fromEpochSeconds(0)
        )

        repository.save(activeHabit)
        repository.save(archivedHabit)

        val activeList = repository.findAllActive()
        assertEquals(1, activeList.size)
        assertEquals("Active", activeList[0].name)
    }

    @Test
    fun `archive habit sets archivedAt to non null`() {
        val habit = Habit(
            id = "3",
            name = "To Archive",
            emoji = null,
            color = "#000",
            frequency = FrequencyType.Daily,
            createdAt = LocalDate(2026, 1, 1),
            archivedAt = null,
            updatedAt = Instant.fromEpochSeconds(0)
        )

        repository.save(habit)
        repository.archive("3")

        val retrieved = repository.findById("3")
        assertNotNull(retrieved)
        assertNotNull(retrieved.archivedAt)
    }
}
