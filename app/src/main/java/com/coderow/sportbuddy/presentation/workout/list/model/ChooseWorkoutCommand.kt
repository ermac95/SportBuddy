package com.coderow.sportbuddy.presentation.workout.list.model

internal sealed interface ChooseWorkoutCommand {
    /**
     * Перейти к выбранной тренировке
     */
    data class OpenWorkout(val id: String) : ChooseWorkoutCommand
}