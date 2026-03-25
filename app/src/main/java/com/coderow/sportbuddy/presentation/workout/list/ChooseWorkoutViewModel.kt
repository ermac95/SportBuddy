package com.coderow.sportbuddy.presentation.workout.list

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.CommandFlow
import com.coderow.sportbuddy.core.utils.emit
import com.coderow.sportbuddy.data.repository.WorkoutRepository
import com.coderow.sportbuddy.domain.WorkoutExerciseTemplate
import com.coderow.sportbuddy.domain.WorkoutTemplate
import com.coderow.sportbuddy.presentation.workout.list.model.ChooseWorkoutCommand
import com.coderow.sportbuddy.presentation.workout.list.model.WorkoutListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ChooseWorkoutViewModel @Inject constructor(
    repository: WorkoutRepository,
) : BaseViewModel() {

    val commandFlow = CommandFlow<ChooseWorkoutCommand>(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutsListFlow: StateFlow<ImmutableList<WorkoutListItem>> = repository.observeAllWorkouts().mapLatest { exercises ->
        val items = exercises.map { it.toUiModel() }
        items.toImmutableList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    private fun WorkoutTemplate.toUiModel(): WorkoutListItem =
        WorkoutListItem(
            id = id,
            name = name,
            exercises = getAllExercisesNames(exercisesList),
            muscleGroups = getAllMuscleGroups(exercisesList),
        )

    private fun getAllExercisesNames(
        exercisesList: List<WorkoutExerciseTemplate>,
    ): String = exercisesList.joinToString(", ") { it.name.orEmpty() }

    private fun getAllMuscleGroups(
        exercisesList: List<WorkoutExerciseTemplate>,
    ): String = exercisesList
        .flatMap { it.muscleGroups }
        .distinct()
        .joinToString(", ") { it.value }

    fun onWorkoutSelect(workoutId: String) {
        commandFlow emit ChooseWorkoutCommand.OpenWorkout(workoutId)
    }
}