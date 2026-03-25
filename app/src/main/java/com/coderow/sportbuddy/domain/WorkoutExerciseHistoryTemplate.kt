package com.coderow.sportbuddy.domain

import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import kotlinx.serialization.Serializable

/**
 * Модель упражнения для хранения инфо в истории тренировок
 *
 * @param id - ИД упражнения в базе истории упражнений
 * @param name - наименование упражнения
 * @param muscleGroups - группы мышцы задействованне в упражнении
 * @param inventoryType - тип инвентаря, задействованного в упражнении
 * @param setsNumber - количество подходов в данном упражнении в рамках выбранной тренирови
 * @param setsInterval - интервал отдыха (сек) между подходами в рамках выбранной тренировки
 * @param setsInfo - информация о всех подходах (количестов повторений/вес)
 */
@Serializable
data class WorkoutExerciseHistoryTemplate(
    val id: String,
    val name: String,
    val muscleGroups: Set<MuscleGroupType>,
    val inventoryType: ExerciseInventoryType?,
    val setsNumber: Int,
    val setsInterval: TimeInterval?,
    val setsInfo: MutableMap<Int, ExerciseSetInfo>,
) {

    /**
     * Инфо о конкретном подходе в упражнении
     *
     * @param reps - количество повторений в подходе
     * @param weight - вес инвентаря, использованный в подходе
     */
    @Serializable
    data class ExerciseSetInfo(
        val reps: Int?,
        val weight: Float?,
    )
}
