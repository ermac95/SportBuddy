package com.coderow.sportbuddy.domain

import java.util.UUID

/**
 * Модель данных для создаваемой тренировки
 */
data class WorkoutTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val intervalBetweenExercises: TimeInterval,
    val exercisesList: List<WorkoutExerciseTemplate>,
)