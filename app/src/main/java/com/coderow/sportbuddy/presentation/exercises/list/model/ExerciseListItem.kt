package com.coderow.sportbuddy.presentation.exercises.list.model

import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType

/**
 * UI элемент списка упражнений
 */
data class ExerciseListItem(
    val name: String,
    val muscleGroups: String,
    val inventoryType: ExerciseInventoryType,
    val inventoryWeight: String?,
)