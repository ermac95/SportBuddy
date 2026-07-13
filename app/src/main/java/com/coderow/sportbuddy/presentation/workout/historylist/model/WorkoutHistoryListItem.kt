package com.coderow.sportbuddy.presentation.workout.historylist.model

import kotlinx.collections.immutable.ImmutableList

/**
 * Элемент списка завершенных тренировок для истории тренировок
 *
 * @param id - ИД завершенной тренировки
 * @param date - дата проведения тренировки
 * @param name - название тренировки
 * @param muscleGroups - группы мышц, задействованные в тренировке
 * @param exercises - список упражнений, включенных в тренировку
 */
data class WorkoutHistoryListItem(
    val id: String,
    val date: String,
    val name: String,
    val exercises: ImmutableList<ExerciseHistoryItem>,
    val muscleGroups: String,
) {

    /**
     * Элемент с информацией об упражнении в составе тренировки
     *
     * @param id - ИД упражнения в истории
     * @param name - название упражнения
     * @param setsInfo - информация о подходах в упражнении
     */
    data class ExerciseHistoryItem(
        val id: String,
        val name: String,
        val setsInfo: ImmutableList<ExerciseSetInfoItem>,
    ) {

        /**
         * Информация о выполненном в упражнении подходе
         *
         * @param reps - количество повторений
         * @param weight - используемый вес
         */
        data class ExerciseSetInfoItem(
            val reps: String,
            val weight: String,
        )
    }
}
