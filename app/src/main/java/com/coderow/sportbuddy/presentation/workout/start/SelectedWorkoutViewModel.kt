package com.coderow.sportbuddy.presentation.workout.start

import androidx.lifecycle.SavedStateHandle
import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.flowOf
import com.coderow.sportbuddy.data.repository.WorkoutHistoryRepository
import com.coderow.sportbuddy.data.repository.WorkoutRepository
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import com.coderow.sportbuddy.domain.WorkoutExerciseTemplate
import com.coderow.sportbuddy.domain.WorkoutHistoryTemplate
import com.coderow.sportbuddy.presentation.workout.start.model.StartWorkoutUiModel
import com.coderow.sportbuddy.presentation.workout.start.model.WorkoutExerciseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class SelectedWorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val historyRepository: WorkoutHistoryRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val workoutId: String = checkNotNull(
        savedStateHandle["workoutId"]
    )

    private val _uiModel = MutableStateFlow<StartWorkoutUiModel>(StartWorkoutUiModel.Idle)
    val uiModel: StateFlow<StartWorkoutUiModel> = _uiModel.asStateFlow()

    private val workoutTemplateFlow = flowOf {
        repository.getWorkoutById(workoutId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private var session: WorkoutSession? = null
    private var timerJob: Job? = null

    init {
        workoutTemplateFlow.map { workoutTemplate ->
            if (workoutTemplate == null) {
                return@map
            } else {
                val currentState = StartWorkoutUiModel.StartWorkout(
                    workoutName = workoutTemplate.name,
                    exercises = workoutTemplate.exercisesList
                        .map { it.toUiModel() }
                        .toPersistentList(),
                )
                updateState(currentState)
            }
        }.launchIn(viewModelScope)
    }

    private fun WorkoutExerciseTemplate.toUiModel(): WorkoutExerciseItem =
        WorkoutExerciseItem(
            id = id,
            name = name.orEmpty(),
            muscleGroups = muscleGroups.joinToString(", ") { it.value },
            inventoryType = inventoryType?.value ?: "",
            repetitionsNumber = setsNumber.toString(),
            repetitionsInterval = setsInterval?.title ?: "",
        )

    private fun updateState(targetState: StartWorkoutUiModel) {
        _uiModel.update { targetState }
    }

    fun onStartTrainingClick() {
        val workout = workoutTemplateFlow.value ?: return

        session = WorkoutSession(
            workoutName = workout.name,
            intervalBetweenExercises = workout.intervalBetweenExercises,
            currentExerciseIndex = 0,
            currentSetIndex = 0,
            exercises = workout.exercisesList.map {
                WorkoutExerciseHistoryTemplate(
                    id = it.id,
                    name = it.name.orEmpty(),
                    muscleGroups = it.muscleGroups,
                    inventoryType = it.inventoryType,
                    setsNumber = it.setsNumber ?: 0,
                    setsInterval = it.setsInterval,
                    setsInfo = mutableMapOf(),
                )

            }.toMutableList()
        )

        showCurrentExercise()
    }

    private fun showCurrentExercise() {
        val session = session ?: return
        val exercise = session.exercises[session.currentExerciseIndex]

        updateState(
            StartWorkoutUiModel.WorkoutExerciseInProcess(
                exercise = exercise,
                isLastRepetition = session.currentSetIndex == exercise.setsNumber - 1
            )
        )
    }

    fun onSetCompleted(
        repsFormatted: String,
        weightFormatted: String
    ) {
        val reps = repsFormatted.toIntOrNull()
        val weight = weightFormatted
            .replace(',', '.')
            .toFloatOrNull()

        val session = session ?: return
        val exercise = session.exercises[session.currentExerciseIndex]
        val currentSetInfo = WorkoutExerciseHistoryTemplate.ExerciseSetInfo(
            reps = reps,
            weight = weight,
        )

        exercise.setsInfo[session.currentSetIndex] = currentSetInfo
        moveForward()
    }

    private fun moveForward() {
        val session = session ?: return
        val exercise = session.exercises[session.currentExerciseIndex]

        val lastSet = session.currentSetIndex == exercise.setsNumber - 1

        if (!lastSet) {
            updateSession {
                it.copy(
                    currentSetIndex = it.currentSetIndex + 1,
                )
            }
            startRestTimer(
                timeInSecs = exercise.setsInterval?.duration,
            )
            return
        }

        val lastExercise = session.currentExerciseIndex == session.exercises.lastIndex

        if (!lastExercise) {
            updateSession {
                it.copy(
                    currentExerciseIndex = it.currentExerciseIndex + 1,
                    currentSetIndex = 0
                )
            }
            startRestTimer(
                timeInSecs = session.intervalBetweenExercises.duration,
            )
            return
        }

        finishWorkout()
    }

    private fun updateSession(
        transform: (WorkoutSession) -> WorkoutSession,
    ) {
        session = session?.let(transform)
    }

    private fun startRestTimer(
        timeInSecs: Duration?
    ) {
        timerJob?.cancel()
        val totalSeconds = timeInSecs?.inWholeSeconds ?: return

        timerJob = viewModelScope.launch {
            for (secondsLeft in totalSeconds downTo 0) {
                updateState(
                    StartWorkoutUiModel.Rest(
                        totalSeconds = totalSeconds.toInt(),
                        secondsRemaining = secondsLeft.toInt(),
                    )
                )

                if (secondsLeft > 0) {
                    delay(1.seconds)
                }
            }

            showCurrentExercise()
        }
    }

    private fun finishWorkout() {
        viewModelScope.launch {
            val session = session ?: return@launch
            val historyTemplate = WorkoutHistoryTemplate(
                name = session.workoutName,
                date = Date(),
                intervalBetweenExercises = session.intervalBetweenExercises,
                exercisesList = session.exercises,
            )

            historyRepository.insertWorkout(historyTemplate)

            updateState(StartWorkoutUiModel.WorkoutCompleted(session.workoutName))
        }
    }

    /**
     * Информация о текущей сессии тренировок
     */
    private data class WorkoutSession(
        val workoutName: String,
        val intervalBetweenExercises: TimeInterval,
        val currentExerciseIndex: Int,
        val currentSetIndex: Int,
        val exercises: MutableList<WorkoutExerciseHistoryTemplate>
    )
}