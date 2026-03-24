package com.coderow.sportbuddy.presentation.workout.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.cardShape
import com.coderow.sportbuddy.core.presentation.roundButtonShape
import com.coderow.sportbuddy.core.utils.clickableWithDebounceAndSoundEffect
import com.coderow.sportbuddy.domain.Exercise
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExercise
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.LightPurple
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.seconds

@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    viewModel: CreateWorkoutViewModel = hiltViewModel()
) {
    val workoutName by viewModel.workoutName.collectAsStateWithLifecycle()
    val exercisesList by viewModel.availableExercises.collectAsStateWithLifecycle()
    val addedExercises by viewModel.addedExercises.collectAsStateWithLifecycle()

    val availableIntervals by viewModel.availableIntervals.collectAsStateWithLifecycle()
    val intervalBetweenExercises by viewModel.intervalBetweenExercises.collectAsStateWithLifecycle()
    val isSaveButtonEnabled by viewModel.isSaveButtonEnabled.collectAsStateWithLifecycle()

    CreateWorkoutScreenContent(
        workoutName = workoutName,
        onWorkoutNameChange = { viewModel.onWorkoutNameChange(it) },
        exercisesList = exercisesList,
        addedExercises = addedExercises,
        availableIntervals = availableIntervals,
        intervalBetweenExercises = intervalBetweenExercises,
        isSaveButtonEnabled = isSaveButtonEnabled,
        onBackButtonClick = { navController.popBackStack() },
        onAddExerciseClick = { viewModel.onAddExerciseClick() },
        onDeleteExerciseClick = { viewModel.deleteExercise(it) },
        onSelectExerciseClick = { templateId, itemId ->
            viewModel.onSelectExercise(templateId, itemId)
        },
        onExerciseIntervalSelect = { templateId, interval ->
            viewModel.onSelectRepetitionInterval(templateId, interval)
        },
        onIntervalBetweenExerciseSelect = {
            viewModel.onSelectIntervalBetweenExercises(it)
        },
        onSaveButtonClick = {
            viewModel.saveWorkout()
        }
    )
}

@Composable
private fun CreateWorkoutScreenContent(
    workoutName: String,
    onWorkoutNameChange: (String) -> Unit,
    exercisesList: ImmutableList<Exercise>,
    addedExercises: ImmutableList<WorkoutExercise>,
    availableIntervals: ImmutableList<TimeInterval>,
    intervalBetweenExercises: TimeInterval?,
    isSaveButtonEnabled: Boolean,
    onAddExerciseClick: () -> Unit,
    onDeleteExerciseClick: (String) -> Unit,
    onSelectExerciseClick: (templateId: String, selectedItemId: String) -> Unit,
    onExerciseIntervalSelect: (templateId: String, interval: TimeInterval) -> Unit,
    onIntervalBetweenExerciseSelect: (interval: TimeInterval) -> Unit,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.create_training_title),
                onBackClick = { onBackButtonClick() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 90.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(
                    key = "workoutNameField"
                ) {
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = onWorkoutNameChange,
                        label = { Text(stringResource(R.string.workout_name)) },
                        placeholder = { Text(stringResource(R.string.workout_name_hint)) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item(
                    key = "intervalBetweenExercises"
                ) {
                    Column {
                        // Выбор интервала между упражнениями
                        Text(
                            text = stringResource(R.string.interval_between_exercises_title),
                            style = typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        TimeSelectorField(
                            selectedValue = intervalBetweenExercises,
                            availableIntervals = availableIntervals,
                            onValueChange = onIntervalBetweenExerciseSelect,
                        )
                    }
                }

                itemsIndexed(addedExercises) { index, exerciseTemplate ->
                    ExerciseTemplateItem(
                        number = index + 1,
                        exerciseTemplate = exerciseTemplate,
                        availableExercises = exercisesList,
                        availableIntervals = availableIntervals,
                        onSelectExerciseClick = onSelectExerciseClick,
                        onTimeIntervalSelect = onExerciseIntervalSelect,
                        onDeleteClick = onDeleteExerciseClick,
                    )
                }

                item(
                    key = "addExerciseButton",
                ) {
                    AddExerciseButton(
                        onClick = onAddExerciseClick,
                    )
                }
            }

            val buttonBackGroundColor = if (isSaveButtonEnabled) {
                LightPurple
            } else {
                Color.DarkGray
            }

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        shape = roundButtonShape,
                        color = buttonBackGroundColor
                    )
                    .padding(16.dp)
                    .clickableWithDebounceAndSoundEffect(enabled = isSaveButtonEnabled) {
                        onSaveButtonClick()
                    },
                style = typography.titleLarge,
                text = stringResource(R.string.save_button),
                textAlign = TextAlign.Center,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ExerciseTemplateItem(
    number: Int,
    exerciseTemplate: WorkoutExercise,
    availableExercises: ImmutableList<Exercise>,
    availableIntervals: ImmutableList<TimeInterval>,
    onSelectExerciseClick: (templateId: String, selectedItemId: String) -> Unit,
    onTimeIntervalSelect: (templateId: String, interval: TimeInterval) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    val borderColor = Color.DarkGray

    Box(
        modifier = Modifier
            .clip(cardShape)
            .background(Color.White)
            .border(
                border = BorderStroke(4.dp, borderColor),
                shape = cardShape
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.exercise_add_number, number),
                    color = Color.Black,
                    style = typography.titleLarge,
                )

                Spacer(modifier = Modifier.weight(1F))

                Icon(
                    modifier = Modifier
                        .size(40.dp, 40.dp)
                        .clickable { onDeleteClick(exerciseTemplate.id) },
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }

            // Выбор упражнения
            Text(
                text = stringResource(R.string.exercise_select_from_list_title),
                style = typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            DropdownExercisesListItem(
                selectedItemTitle = exerciseTemplate.name,
                items = availableExercises,
                onItemClick = { itemId ->
                    onSelectExerciseClick(exerciseTemplate.id, itemId)
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор интервала между подходами
            Text(
                text = stringResource(R.string.exercise_repetition_interval),
                style = typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            TimeSelectorField(
                selectedValue = exerciseTemplate.repetitionsInterval,
                onValueChange = { selectedInterval ->
                    onTimeIntervalSelect(exerciseTemplate.id, selectedInterval)
                },
                availableIntervals = availableIntervals,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownExercisesListItem(
    selectedItemTitle: String?,
    items: ImmutableList<Exercise>,
    onItemClick: (itemId: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .background(
                shape = cardShape,
                color = Color.Transparent
            )
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedCard(
            modifier = Modifier
                .menuAnchor(PrimaryNotEditable, true)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedItemTitle ?: stringResource(R.string.exercise_select_title),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onItemClick(item.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectorField(
    modifier: Modifier = Modifier,
    selectedValue: TimeInterval?,
    availableIntervals: ImmutableList<TimeInterval>,
    onValueChange: (TimeInterval) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedCard(
            modifier = Modifier
                .menuAnchor(PrimaryNotEditable, true)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedValue?.title ?: stringResource(R.string.exercise_repetition_interval_select),
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableIntervals.forEach { interval ->
                DropdownMenuItem(
                    text = { Text(text = interval.title) },
                    onClick = {
                        onValueChange(interval)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AddExerciseButton(
    onClick: () -> Unit,
) {
    val borderColor = Color.DarkGray

    Box(
        modifier = Modifier
            .clip(cardShape)
            .background(Color.White)
            .border(
                border = BorderStroke(4.dp, borderColor),
                shape = cardShape
            )
            .clickableWithDebounceAndSoundEffect {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(80.dp, 80.dp),
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.exercise_add_title),
                textAlign = TextAlign.Center,
                style = typography.titleLarge,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CreateWorkoutPreview() {
    SportBuddyTheme {
        CreateWorkoutScreenContent(
            workoutName = "",
            onWorkoutNameChange = {},
            exercisesList = persistentListOf(),
            addedExercises = persistentListOf(
                WorkoutExercise(
                    id = "1",
                    name = null,
                    muscleGroups = emptySet(),
                    inventoryType = null,
                    repetitionsInterval = null,
                )
            ),
            availableIntervals = persistentListOf(
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
            ),
            intervalBetweenExercises = null,
            isSaveButtonEnabled = true,
            onAddExerciseClick = {},
            onDeleteExerciseClick = {},
            onSelectExerciseClick = { _, _ -> },
            onExerciseIntervalSelect = { _, _ -> },
            onIntervalBetweenExerciseSelect = {},
            onBackButtonClick = {},
            onSaveButtonClick = {},
        )
    }
}
