package com.coderow.sportbuddy.presentation.workout.list.model

/**
 * Элемент списка доступных тренировок
 */
data class WorkoutListItem(
    val id: String,
    val name: String,
    val exercises: String,
    val muscleGroups: String,
)
