package com.coderow.sportbuddy.presentation.workout.historylist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.cardShape
import com.coderow.sportbuddy.core.utils.clickableWithDebounceAndSoundEffect
import com.coderow.sportbuddy.core.utils.observe
import com.coderow.sportbuddy.presentation.WorkoutHistoryDetails
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import com.coderow.sportbuddy.presentation.workout.historydetails.model.HistoryWorkoutCommand
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun HistoryWorkoutScreen(
    navController: NavController,
    viewModel: HistoryWorkoutViewModel = hiltViewModel(),
) {

    val workoutsList by viewModel.workoutsListFlow.collectAsStateWithLifecycle()

    viewModel.commandFlow.observe { command ->
        when (command) {
            is HistoryWorkoutCommand.OpenWorkoutDetails -> {
                navController.navigate(WorkoutHistoryDetails(command.id))
            }
        }
    }

    HistoryWorkoutScreenContent(
        items = workoutsList,
        onBackButtonClick = { navController.popBackStack() },
        onWorkoutClick = viewModel::onWorkoutClick,
    )
}

@Composable
private fun HistoryWorkoutScreenContent(
    items: ImmutableList<WorkoutHistoryListItem>,
    onBackButtonClick: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.training_history),
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
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { workout ->
                    WorkoutCard(
                        item = workout,
                        onWorkoutClick = onWorkoutClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    item: WorkoutHistoryListItem,
    onWorkoutClick: (workoutId: String) -> Unit,
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
                onWorkoutClick(item.id)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            // Дата тренировки
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = item.date,
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.titleLarge
            )

            // Название тренировки
            FlowRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.exercise_title),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.name,
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
                    text = item.muscleGroups,
                    color = Color.Black,
                )
            }

            // Список упражнений
            // Дата тренировки
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.workout_exercises_point_title),
                textAlign = TextAlign.Start,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            item.exercises.forEachIndexed { index, exerciseItem ->
                // Номер и название упражнения
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "${index + 1}. ${exerciseItem.name}",
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun WorkoutHistoryScreenPreview() {
    SportBuddyTheme {
        HistoryWorkoutScreenContent(
            items = persistentListOf(
                WorkoutHistoryListItem(
                    id = "12312",
                    name = "Грудь + трицепс",
                    date = "25 июня 2026 г.",
                    muscleGroups = "Грудь, Трицепс, Предплечье",
                    exercises = persistentListOf(
                        WorkoutHistoryListItem.ExerciseHistoryItem(
                            id = "123123",
                            name = "Подтягивания на турнике",
                            setsInfo = persistentListOf(
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "10",
                                    weight = "",
                                ),
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "10",
                                    weight = "",
                                ),
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "8",
                                    weight = "",
                                )
                            )
                        ),
                        WorkoutHistoryListItem.ExerciseHistoryItem(
                            id = "124235352",
                            name = "Подъем гантели на трицепс",
                            setsInfo = persistentListOf(
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "10",
                                    weight = "7.5",
                                ),
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "9",
                                    weight = "7.5",
                                ),
                                WorkoutHistoryListItem.ExerciseHistoryItem.ExerciseSetInfoItem(
                                    reps = "5",
                                    weight = "7.5",
                                )
                            )
                        ),
                    )
                ),
                WorkoutHistoryListItem(
                    id = "123132",
                    name = "Спина + бицепс",
                    date = "27 июня 2026 г.",
                    muscleGroups = "Широчайшие мышцы, Спина, Предплечье",
                    exercises = persistentListOf(),
                )
            ),
            onBackButtonClick = {},
            onWorkoutClick = {},
        )
    }
}