package com.coderow.sportbuddy.domain

import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType

/**
 * Доменная модель с информацией об упражнении
 */
data class Exercise(
    val id: String,
    val name: String,
    val muscleGroups: Set<MuscleGroupType>,
    val inventoryType: ExerciseInventoryType?,
)