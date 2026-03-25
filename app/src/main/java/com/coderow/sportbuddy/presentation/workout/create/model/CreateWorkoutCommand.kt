package com.coderow.sportbuddy.presentation.workout.create.model

internal sealed interface CreateWorkoutCommand {
    /**
     * Закрыть экран
     */
    data object ExitScreen : CreateWorkoutCommand
}