package com.coderow.sportbuddy.presentation.workout.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.roundButtonShape
import com.coderow.sportbuddy.core.utils.clickableWithDebounceAndSoundEffect
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import com.coderow.sportbuddy.presentation.MainScreen
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.LightGray
import com.coderow.sportbuddy.presentation.ui.theme.LightPurple
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import com.coderow.sportbuddy.presentation.workout.start.model.StartWorkoutUiModel
import com.coderow.sportbuddy.presentation.workout.start.model.WorkoutExerciseItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun SelectedWorkoutScreen(
    navController: NavController,
    viewModel: SelectedWorkoutViewModel = hiltViewModel(),
) {

    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    SelectedWorkoutScreenContent(
        uiModel = uiModel,
        onStartTrainingClick = { viewModel.onStartTrainingClick() },
        onBackButtonClick = { navController.popBackStack() },
        onExerciseButtonClick = viewModel::onSetCompleted,
        onExitClick = {
            navController.navigate(MainScreen) {
                popUpTo(MainScreen) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    )
}

@Composable
private fun SelectedWorkoutScreenContent(
    uiModel: StartWorkoutUiModel,
    onStartTrainingClick: () -> Unit,
    onBackButtonClick: () -> Unit,
    onExerciseButtonClick: (reps: String, weight: String) -> Unit,
    onExitClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.workout_toolbar_title),
                onBackClick = { onBackButtonClick() }
            )
        }
    ) { innerPadding ->
        when (uiModel) {
            is StartWorkoutUiModel.StartWorkout -> {
                StartWorkoutScreenContent(
                    padding = innerPadding,
                    workoutName = uiModel.workoutName,
                    exercises = uiModel.exercises,
                    onStartTrainingClick = onStartTrainingClick,
                )
            }
            is StartWorkoutUiModel.WorkoutExerciseInProcess -> {
                WorkoutExerciseInProcessScreenContent(
                    padding = innerPadding,
                    uiModel = uiModel,
                    onButtonClick = onExerciseButtonClick,
                )
            }

            is StartWorkoutUiModel.Rest -> {
                RestTimerScreenContent(
                    secondsLeft = uiModel.secondsRemaining,
                    totalSeconds = uiModel.totalSeconds,
                )
            }

            is StartWorkoutUiModel.WorkoutCompleted -> {
                WorkoutCompletedScreenContent(
                    workoutName = uiModel.workoutName,
                    onExitClick = onExitClick,
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun StartWorkoutScreenContent(
    padding: PaddingValues,
    workoutName: String,
    exercises: ImmutableList<WorkoutExerciseItem>,
    onStartTrainingClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(LightGray),
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 100.dp)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(
                key = "workoutTitleBlock",
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        )
                        .padding(24.dp),
                    text = stringResource(R.string.selected_workout_title, workoutName),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            item(
                key = "exercisesInfoBlock"
            ) {
                ExercisesInfoBlock(exercises)
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    shape = roundButtonShape,
                    color = LightPurple
                )
                .padding(16.dp)
                .clickableWithDebounceAndSoundEffect {
                    onStartTrainingClick()
                },
            style = typography.titleLarge,
            text = stringResource(R.string.start_button),
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@Composable
private fun ExercisesInfoBlock(
    exercises: ImmutableList<WorkoutExerciseItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ),
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.exercises_list_title),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = typography.titleLarge,
            textAlign = TextAlign.Center
        )

        exercises.forEach { exercise ->
            ExerciseItem(
                exercise = exercise,
            )
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: WorkoutExerciseItem,
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        // Название упражнения
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise.name,
                color = Color.Black,
            )
        }

        // Группы мышц
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_muscles_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise.muscleGroups,
                color = Color.Black,
            )
        }

        // Тип снаряда
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_inventory_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise.inventoryType,
                color = Color.Black,
            )
        }

        // Количество подходов
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_repetition_number_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise.repetitionsNumber,
                color = Color.Black,
            )
        }

        // Интервал между подходами
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.exercise_repetition_interval_title),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = exercise.repetitionsInterval,
                color = Color.Black,
            )
        }
    }
}

// Процесс выполнения упражнения
@Composable
private fun WorkoutExerciseInProcessScreenContent(
    padding: PaddingValues,
    uiModel: StartWorkoutUiModel.WorkoutExerciseInProcess,
    onButtonClick: (reps: String, weight: String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(LightGray),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                .background(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ),
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.selected_exercise_title, uiModel.exercise.name),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.titleLarge,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = reps,
                onValueChange = { reps = it },
                label = { Text(stringResource(R.string.repetition_number_hint)) },
                placeholder = { Text(stringResource(R.string.repetition_number_hint)) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = weight,
                onValueChange = { weight = it },
                label = { Text(stringResource(R.string.exercise_weight_hint)) },
                placeholder = { Text(stringResource(R.string.exercise_weight_hint)) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                singleLine = true
            )
        }

        val buttonText = if (uiModel.isLastRepetition) {
            stringResource(R.string.complete_exercise_button)
        } else {
            stringResource(R.string.rest_button)
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    shape = roundButtonShape,
                    color = LightPurple,
                )
                .padding(16.dp)
                .clickableWithDebounceAndSoundEffect {
                    onButtonClick(reps, weight)
                },
            style = typography.titleLarge,
            text = buttonText,
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

//Таймер на перерыв между подходами/упражнениями
@Composable
fun RestTimerScreenContent(
    modifier: Modifier = Modifier,
    secondsLeft: Int,
    totalSeconds: Int,
) {
    val progress = remember(secondsLeft, totalSeconds) {
        if (totalSeconds == 0) 0f
        else secondsLeft.toFloat() / totalSeconds
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(220.dp),
            strokeWidth = 12.dp,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = secondsLeft.toString(),
                style = typography.displayLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "seconds",
                style = typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun WorkoutCompletedScreenContent(
    workoutName: String,
    onExitClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            text = stringResource(R.string.workout_completed_title, workoutName),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    shape = roundButtonShape,
                    color = LightPurple
                )
                .padding(16.dp)
                .clickableWithDebounceAndSoundEffect {
                    onExitClick()
                },
            style = typography.titleLarge,
            text = stringResource(R.string.workout_exit_button),
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@PreviewLightDark
@Composable
fun StartWorkoutScreenPreview() {
    SportBuddyTheme {
        SelectedWorkoutScreenContent(
            uiModel = StartWorkoutUiModel.StartWorkout(
                workoutName = "Грудь + Трицепс",
                exercises = persistentListOf(
                    WorkoutExerciseItem(
                        id = "1",
                        name = "Жим лежа",
                        muscleGroups = "Верхние грудные, трицепс",
                        inventoryType = "Штанга",
                        repetitionsNumber = "3",
                        repetitionsInterval = "45 сек",
                    ),
                    WorkoutExerciseItem(
                        id = "1",
                        name = "Жим лежа",
                        muscleGroups = "Верхние грудные, трицепс",
                        inventoryType = "Штанга",
                        repetitionsNumber = "3",
                        repetitionsInterval = "45 сек",
                    ),
                    WorkoutExerciseItem(
                        id = "1",
                        name = "Жим лежа",
                        muscleGroups = "Верхние грудные, трицепс",
                        inventoryType = "Штанга",
                        repetitionsNumber = "3",
                        repetitionsInterval = "45 сек",
                    ),
                )
            ),
            onStartTrainingClick = {},
            onBackButtonClick = {},
            onExerciseButtonClick = { _, _ -> },
            onExitClick = {},
        )
    }
}

@PreviewLightDark
@Composable
fun WorkoutInProcessScreenPreview() {
    SportBuddyTheme {
        SelectedWorkoutScreenContent(
            uiModel = StartWorkoutUiModel.WorkoutExerciseInProcess(
                exercise = WorkoutExerciseHistoryTemplate(
                    id = "1",
                    name = "Жим лежа",
                    muscleGroups = persistentSetOf(
                        MuscleGroupType.UPPER_CHEST,
                        MuscleGroupType.TRICEPS,
                    ),
                    inventoryType = ExerciseInventoryType.BARBELL,
                    setsNumber = 3,
                    setsInterval = TimeInterval(
                        title = "45 сек",
                        duration = 45.seconds,
                    ),
                    setsInfo = mutableMapOf(),
                ),
                isLastRepetition = false,
            ),
            onStartTrainingClick = {},
            onBackButtonClick = {},
            onExerciseButtonClick = { _, _ -> },
            onExitClick = {},
        )
    }
}

@PreviewLightDark
@Composable
fun RestTimerScreenPreview() {
    SportBuddyTheme {
        SelectedWorkoutScreenContent(
            uiModel = StartWorkoutUiModel.Rest(
                totalSeconds = 60,
                secondsRemaining = 32,
            ),
            onStartTrainingClick = {},
            onBackButtonClick = {},
            onExerciseButtonClick = { _, _ -> },
            onExitClick = {},
        )
    }
}

@PreviewLightDark
@Composable
fun WorkoutCompletedScreenPreview() {
    SportBuddyTheme {
        WorkoutCompletedScreenContent(
            workoutName = "День подтягиваний",
            onExitClick = {},
        )
    }
}