package com.coderow.sportbuddy.presentation.workout.historydetails

import androidx.lifecycle.SavedStateHandle
import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.flowOf
import com.coderow.sportbuddy.data.repository.WorkoutHistoryRepository
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate.ExerciseSetInfo
import com.coderow.sportbuddy.domain.WorkoutHistoryTemplate
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem.ExerciseHistoryItem
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryWorkoutDetailsViewModel @Inject constructor(
    repository: WorkoutHistoryRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val workoutId: String = checkNotNull(
        savedStateHandle["workoutId"]
    )

    val selectedWorkoutFlow = flowOf {
        val selectedWorkout = repository.getWorkoutById(workoutId) ?: return@flowOf null
        selectedWorkout.toUiModel()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

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
}