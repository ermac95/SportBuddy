package com.coderow.sportbuddy.presentation.exercises.create.model

import kotlinx.serialization.Serializable

/**
 * Тип инвентаря, используемый в упражнении
 */
@Serializable
enum class ExerciseInventoryType(val value: String) {
    DUMBBELL("Гантели"),
    BARBELL("Штанга"),
    HORIZONTAL_BAR("Турник"),
    SELF_WEIGHT("Свой вес"),
    ;
}