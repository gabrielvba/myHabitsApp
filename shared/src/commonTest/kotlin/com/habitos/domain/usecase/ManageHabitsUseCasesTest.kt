package com.habitos.domain.usecase

import com.habitos.domain.model.FrequencyType
import com.habitos.domain.repository.FakeHabitRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ManageHabitsUseCasesTest {

    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var createHabitUseCase: CreateHabitUseCase
    private lateinit var archiveHabitUseCase: ArchiveHabitUseCase
    private lateinit var getActiveHabitsUseCase: GetActiveHabitsUseCase

    @BeforeTest
    fun setup() {
        habitRepository = FakeHabitRepository()
        createHabitUseCase = CreateHabitUseCase(habitRepository)
        archiveHabitUseCase = ArchiveHabitUseCase(habitRepository)
        getActiveHabitsUseCase = GetActiveHabitsUseCase(habitRepository)
    }

    @AfterTest
    fun tearDown() {
        habitRepository.clear()
    }

    @Test
    fun `CreateHabitUseCase with empty name throws IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            createHabitUseCase(
                name = "   ",
                emoji = null,
                color = "#000",
                frequency = FrequencyType.Daily
            )
        }
        assertEquals("O nome do hábito não pode ser vazio", exception.message)
    }

    @Test
    fun `CreateHabitUseCase returns Habit with generated ID and null archivedAt`() {
        val habit = createHabitUseCase(
            name = "Meditar",
            emoji = "🧘‍♂️",
            color = "#123456",
            frequency = FrequencyType.Daily
        )

        assertNotNull(habit.id)
        assertEquals("Meditar", habit.name)
        assertNull(habit.archivedAt)
        assertNotNull(habitRepository.findById(habit.id))
    }

    @Test
    fun `ArchiveHabitUseCase sets archivedAt on habit`() {
        val habit = createHabitUseCase(
            name = "Beber água",
            emoji = "💧",
            color = "#000",
            frequency = FrequencyType.Daily
        )
        assertNull(habit.archivedAt)

        archiveHabitUseCase(habit.id)

        val archivedHabit = habitRepository.findById(habit.id)
        assertNotNull(archivedHabit)
        assertNotNull(archivedHabit.archivedAt)
    }

    @Test
    fun `GetActiveHabitsUseCase returns only non archived habits`() {
        val habit1 = createHabitUseCase("Active", null, "#000", FrequencyType.Daily)
        val habit2 = createHabitUseCase("To Archive", null, "#000", FrequencyType.Daily)

        archiveHabitUseCase(habit2.id)

        val activeHabits = getActiveHabitsUseCase()

        assertEquals(1, activeHabits.size)
        assertEquals(habit1.id, activeHabits[0].id)
    }
}
