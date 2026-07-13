package com.coderow.sportbuddy.presentation.workout.historylist

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.CommandFlow
import com.coderow.sportbuddy.core.utils.emit
import com.coderow.sportbuddy.data.repository.WorkoutHistoryRepository
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate.ExerciseSetInfo
import com.coderow.sportbuddy.domain.WorkoutHistoryTemplate
import com.coderow.sportbuddy.presentation.workout.historydetails.model.HistoryWorkoutCommand
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem.ExerciseHistoryItem
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class HistoryWorkoutViewModel @Inject constructor(
    repository: WorkoutHistoryRepository,
) : BaseViewModel()  {

    val commandFlow = CommandFlow<HistoryWorkoutCommand>(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutsListFlow: StateFlow<ImmutableList<WorkoutHistoryListItem>> = repository.observeAllWorkouts().mapLatest { exercises ->
        val items = exercises.map { it.toUiModel() }
        items.toImmutableList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    private val formatter = SimpleDateFormat(
        "d MMMM yyyy 'г.'",
        Locale("ru")
    )

    private fun WorkoutHistoryTemplate.toUiModel(): WorkoutHistoryListItem =
        WorkoutHistoryListItem(
            id = id,
            name = name,
            date = formatter.format(date),
            muscleGroups = getAllMuscleGroups(exercisesList),
            exercises = exercisesList.map { it.toUiModel() }.toPersistentList(),
        )

    private fun getAllMuscleGroups(
        exercisesList: List<WorkoutExerciseHistoryTemplate>,
    ): String = exercisesList
        .flatMap { it.muscleGroups }
        .distinct()
        .joinToString(", ") { it.value }

    private fun WorkoutExerciseHistoryTemplate.toUiModel(): ExerciseHistoryItem =
        ExerciseHistoryItem(
            id = id,
            name = name,
            setsInfo = setsInfo.values.toList().map { it.toUiModel() }.toPersistentList()
        )

    private fun ExerciseSetInfo.toUiModel(): ExerciseSetInfoItem =
        ExerciseSetInfoItem(
            reps = reps.toString(),
            weight = weight.toString(),
        )

    fun onWorkoutClick(workoutId: String) {
        commandFlow emit HistoryWorkoutCommand.OpenWorkoutDetails(workoutId)
    }
}