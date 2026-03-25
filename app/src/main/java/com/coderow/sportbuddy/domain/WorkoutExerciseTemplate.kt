package com.coderow.sportbuddy.domain

import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import kotlinx.serialization.Serializable

/**
 * Модель шаблона упражнения для привязки к тренировке
 */
@Serializable
data class WorkoutExerciseTemplate(
    val id: String,
    val name: String?,
    val muscleGroups: Set<MuscleGroupType>,
    val inventoryType: ExerciseInventoryType?,
    val setsNumber: Int?,
    val setsInterval: TimeInterval?,
)
