package com.coderow.sportbuddy.presentation.workout.create

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.data.repository.ExerciseRepository
import com.coderow.sportbuddy.data.repository.WorkoutRepository
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExercise
import com.coderow.sportbuddy.domain.WorkoutTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CreateWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    exerciseRepository: ExerciseRepository,
) : BaseViewModel() {

    private val _workoutName = MutableStateFlow("")
    val workoutName = _workoutName.asStateFlow()

    private val _intervalBetweenExercises = MutableStateFlow<TimeInterval?>(null)
    val intervalBetweenExercises: StateFlow<TimeInterval?> = _intervalBetweenExercises.asStateFlow()

    private val _addedExercises = MutableStateFlow(persistentListOf<WorkoutExercise>())
    val addedExercises = _addedExercises.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    val availableExercises = exerciseRepository.observeAllExercises().map { it.toPersistentList() }
        .stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    val isSaveButtonEnabled = combine(
        workoutName,
        intervalBetweenExercises,
        addedExercises
    ) { name, interval, exercises ->
        name.isNotBlank() && interval != null && exercises.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val availableIntervals = flowOf(
        persistentListOf(
            TimeInterval(
                title = "30 сек",
                duration = 35.seconds,
            ),
            TimeInterval(
                title = "45 сек",
                duration = 45.seconds,
            ),
            TimeInterval(
                title = "1 мин",
                duration = 60.seconds,
            ),
            TimeInterval(
                title = "1 мин 15 сек",
                duration = 75.seconds,
            ),
            TimeInterval(
                title = "1 мин 30 сек",
                duration = 90.seconds,
            ),
        )
    ).stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    fun onAddExerciseClick() {
        val newExercise = WorkoutExercise(
            id = UUID.randomUUID().toString(),
            name = null,
            muscleGroups = emptySet(),
            inventoryType = null,
            repetitionsInterval = null,
        )

        _addedExercises.update { it.add(newExercise) }
    }

    fun deleteExercise(exerciseId: String) {
        val exerciseToRemove = _addedExercises.value.firstOrNull { it.id == exerciseId } ?: return
        _addedExercises.update { it.remove(exerciseToRemove) }
    }

    fun onSelectExercise(
        templateId: String,
        itemId: String,
    ) {
        val selectedExercise = availableExercises.value.firstOrNull { it.id == itemId } ?: return

        _addedExercises.update { exercises ->
            exercises.map { templateExercise ->
                if (templateExercise.id == templateId) {
                    templateExercise.copy(
                        name = selectedExercise.name,
                        muscleGroups = selectedExercise.muscleGroups,
                        inventoryType = selectedExercise.inventoryType,
                    )
                } else {
                    templateExercise
                }
            }.toPersistentList()
        }
    }

    fun onSelectRepetitionInterval(
        templateId: String,
        interval: TimeInterval,
    ) {
        _addedExercises.update { exercises ->
            exercises.map { templateExercise ->
                if (templateExercise.id == templateId) {
                    templateExercise.copy(
                        repetitionsInterval = interval,
                    )
                } else {
                    templateExercise
                }
            }.toPersistentList()
        }
    }

    fun onWorkoutNameChange(name: String) {
        _workoutName.value = name
    }

    fun onSelectIntervalBetweenExercises(
        interval: TimeInterval,
    ) {
        _intervalBetweenExercises.update { interval }
    }

    fun saveWorkout() {
        val intervalBetweenExercises = intervalBetweenExercises.value ?: return

        val workoutTemplate = WorkoutTemplate(
            name = workoutName.value,
            intervalBetweenExercises = intervalBetweenExercises,
            exercisesList = addedExercises.value
        )

        viewModelScope.launch {
            workoutRepository.insertWorkout(workoutTemplate)
        }
    }
}