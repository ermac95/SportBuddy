package com.coderow.sportbuddy.presentation.exercises.create.model

/**
 * Тип инвентаря, используемого в упражнении
 */
data class InventoryItem(
    val type: ExerciseInventoryType,
    val isSelected: Boolean,
)