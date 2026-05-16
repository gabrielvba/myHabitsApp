package com.habitos.domain.repository

import com.habitos.domain.model.Habit

class FakeHabitRepository : HabitRepository {
    private val habits = mutableMapOf<String, Habit>()

    override fun save(habit: Habit) {
        habits[habit.id] = habit
    }

    override fun findById(id: String): Habit? {
        return habits[id]
    }

    override fun findAllActive(): List<Habit> {
        return habits.values.filter { it.archivedAt == null }
    }

    override fun archive(id: String) {
        val habit = habits[id]
        if (habit != null) {
            // No banco real isso seta o archivedAt para o valor do momento.
            // Para o Fake, precisamos de uma cópia com a data preenchida. Como o Clock só
            // entra no HT-05, vamos mockar com uma data fictícia aqui ou usar o now.
            // Por simplicidade, adicionamos uma data estática (ex 2026-01-01) apenas para arquivar no fake
            // já que LocalDate.now() não deve ser usado se possível sem clock wrapper
            habits[id] = habit.copy(archivedAt = kotlinx.datetime.LocalDate(2026, 1, 1))
        }
    }

    fun clear() {
        habits.clear()
    }
}
