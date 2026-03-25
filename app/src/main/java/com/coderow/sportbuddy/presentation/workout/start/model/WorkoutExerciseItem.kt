package com.coderow.sportbuddy.presentation.workout.start.model

/**
 * Элемент списка упражнений на экране начала тренировки
 */
data class WorkoutExerciseItem(
    val id: String,
    val name: String,
    val muscleGroups: String,
    val inventoryType: String,
    val repetitionsNumber: String,
    val repetitionsInterval: String,
)
