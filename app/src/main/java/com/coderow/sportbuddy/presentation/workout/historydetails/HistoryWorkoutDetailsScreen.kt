package com.coderow.sportbuddy.presentation.workout.historydetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import com.coderow.sportbuddy.presentation.workout.historylist.model.WorkoutHistoryListItem
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun HistoryWorkoutDetailsScreen(
    navController: NavController,
    viewModel: HistoryWorkoutDetailsViewModel = hiltViewModel(),
) {

    val selectedWorkout by viewModel.selectedWorkoutFlow.collectAsStateWithLifecycle()

    HistoryWorkoutDetailsScreenContent(
        item = selectedWorkout,
        onBackButtonClick = { navController.popBackStack() }
    )
}

@Composable
private fun HistoryWorkoutDetailsScreenContent(
    item: WorkoutHistoryListItem?,
    onBackButtonClick: () -> Unit,
) {
    if (item == null) return

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
            WorkoutCard(
                item = item,
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    item: WorkoutHistoryListItem,
) {
    Box(
        modifier = Modifier
            .clip(cardShape)
            .background(Color.White),
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
                textAlign = TextAlign.Center,
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
            item.exercises.forEachIndexed { index, exerciseItem ->
                // Номер и название упражнения
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    text = "${index + 1}. ${exerciseItem.name}",
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )

                exerciseItem.setsInfo.forEachIndexed { index, item ->
                    // Номер подхода
                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        text = stringResource(R.string.exercise_rep_number, index + 1),
                        textAlign = TextAlign.Start,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )

                    // Количество повторений
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.repetition_number_point_title),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = item.reps,
                            color = Color.Black,
                        )
                    }
                    // Используемый вес
                    if (item.weight.isNotBlank()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.repetition_weight_point_title),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.repetition_weight_kg_value, item.weight),
                                color = Color.Black,
                            )
                        }
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
fun HistoryWorkoutDetailsScreenPreview() {
    SportBuddyTheme {
        HistoryWorkoutDetailsScreenContent(
            item = WorkoutHistoryListItem(
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
            onBackButtonClick = {},
        )
    }
}