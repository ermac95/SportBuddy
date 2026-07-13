package com.coderow.sportbuddy.presentation.workout.historydetails.model

internal sealed interface HistoryWorkoutCommand {
    /**
     * Открыть детализированную историю тренировки
     */
    data class OpenWorkoutDetails(val id: String) : HistoryWorkoutCommand
}