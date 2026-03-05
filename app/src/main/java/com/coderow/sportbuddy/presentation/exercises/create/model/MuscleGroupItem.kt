package com.coderow.sportbuddy.presentation.exercises.create.model

import com.coderow.sportbuddy.domain.MuscleGroupType

/**
 * UI модель для списка выбираемых при создании упражнении групп мышц
 */
data class MuscleGroupItem(
    val muscleGroup: MuscleGroupType,
    val isSelected: Boolean,
)
