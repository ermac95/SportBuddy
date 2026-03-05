package com.coderow.sportbuddy.presentation.exercises.create.model

internal sealed interface CreateExerciseCommand {
    /**
     * Закрыть экран
     */
    data object ExitScreen : CreateExerciseCommand
}