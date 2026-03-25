package com.coderow.sportbuddy.presentation.workout.start.model

import androidx.compose.runtime.Immutable
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import kotlinx.collections.immutable.ImmutableList

/**
 * Состояния экрана тренировки
 */
internal sealed interface StartWorkoutUiModel {

    /**
     * Первичный стейт
     */
    data object Idle : StartWorkoutUiModel

    /**
     * Стейт начала тренировки
     */
    @Immutable
    data class StartWorkout(
        val workoutName: String,
        val exercises: ImmutableList<WorkoutExerciseItem>,
    ) : StartWorkoutUiModel

    /**
     * Стейт выполнения упражнения
     *
     * @property exercise - информация об упражнении
     * @property isRestTime - период отдыха между подходами
     */
    @Immutable
    data class WorkoutExerciseInProcess(
        val exercise: WorkoutExerciseHistoryTemplate,
        val isLastRepetition: Boolean,
    ) : StartWorkoutUiModel

    /**
     * Стейт отдыха между подходами/упражнениями
     */
    @Immutable
    data class Rest(
        val totalSeconds: Int,
        val secondsRemaining: Int,
    ) : StartWorkoutUiModel

    @Immutable
    data class WorkoutCompleted(
        val workoutName: String,
    ) : StartWorkoutUiModel
}