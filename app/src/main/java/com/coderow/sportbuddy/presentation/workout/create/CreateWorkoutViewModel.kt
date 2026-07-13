package com.coderow.sportbuddy.presentation.workout.create

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.CommandFlow
import com.coderow.sportbuddy.core.utils.emit
import com.coderow.sportbuddy.data.repository.ExerciseRepository
import com.coderow.sportbuddy.data.repository.WorkoutRepository
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExerciseTemplate
import com.coderow.sportbuddy.domain.WorkoutTemplate
import com.coderow.sportbuddy.presentation.workout.create.model.CreateWorkoutCommand
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
internal class CreateWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    exerciseRepository: ExerciseRepository,
) : BaseViewModel() {

    val commandFlow = CommandFlow<CreateWorkoutCommand>(viewModelScope)

    private val _workoutName = MutableStateFlow("")
    val workoutName = _workoutName.asStateFlow()

    private val _intervalBetweenExercises = MutableStateFlow<TimeInterval?>(null)
    val intervalBetweenExercises: StateFlow<TimeInterval?> = _intervalBetweenExercises.asStateFlow()

    private val _addedExercises = MutableStateFlow(persistentListOf<WorkoutExerciseTemplate>())
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

    val repetitionNumbersFlow = flowOf(
        persistentListOf(1, 2, 3, 4, 5)
    ).stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    val availableIntervals = flowOf(
        persistentListOf(
            TimeInterval(
                title = "30 сек",
                duration = 30.seconds,
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
        val newExercise = WorkoutExerciseTemplate(
            id = UUID.randomUUID().toString(),
            name = null,
            muscleGroups = emptySet(),
            inventoryType = null,
            setsNumber = null,
            setsInterval = null,
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
                        setsInterval = interval,
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

    fun onSelectRepetitionNumber(
        templateId: String,
        number: Int,
    ) {
        _addedExercises.update { exercises ->
            exercises.map { templateExercise ->
                if (templateExercise.id == templateId) {
                    templateExercise.copy(
                        setsNumber = number,
                    )
                } else {
                    templateExercise
                }
            }.toPersistentList()
        }
    }

    fun onSelectIntervalBetweenExercises(
        interval: TimeInterval,
    ) {
        _intervalBetweenExercises.update { interval }
    }

    fun saveWorkout() {
        viewModelScope.launch {
            val intervalBetweenExercises = intervalBetweenExercises.value ?: return@launch

            val workoutTemplate = WorkoutTemplate(
                name = workoutName.value,
                intervalBetweenExercises = intervalBetweenExercises,
                exercisesList = addedExercises.value
            )

            workoutRepository.insertWorkout(workoutTemplate)
            commandFlow emit CreateWorkoutCommand.ExitScreen
        }
    }
}