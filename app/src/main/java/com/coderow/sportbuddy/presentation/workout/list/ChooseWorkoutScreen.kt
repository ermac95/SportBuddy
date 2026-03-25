package com.coderow.sportbuddy.presentation.workout.list

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
import com.coderow.sportbuddy.presentation.SelectedWorkout
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import com.coderow.sportbuddy.presentation.workout.list.model.ChooseWorkoutCommand
import com.coderow.sportbuddy.presentation.workout.list.model.WorkoutListItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ChooseWorkoutScreen(
    navController: NavController,
    viewModel: ChooseWorkoutViewModel = hiltViewModel()
) {
    val workoutsList by viewModel.workoutsListFlow.collectAsStateWithLifecycle()

    viewModel.commandFlow.observe { command ->
        when (command) {
            is ChooseWorkoutCommand.OpenWorkout -> {
                navController.navigate(SelectedWorkout(command.id))
            }
        }
    }

    ChooseWorkoutScreenContent(
        items = workoutsList,
        onItemClick = { workoutId -> viewModel.onWorkoutSelect(workoutId) },
        onBackButtonClick = { navController.popBackStack() }
    )
}

@Composable
private fun ChooseWorkoutScreenContent(
    items: ImmutableList<WorkoutListItem>,
    onItemClick: (id: String) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.workouts_list_title),
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
                    .padding(bottom = 100.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { workout ->
                    WorkoutCard(
                        item = workout,
                        onItemClick = onItemClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    item: WorkoutListItem,
    onItemClick: (id: String) -> Unit,
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
                onItemClick(item.id)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            // Название тренировки
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = item.name,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.titleLarge
            )

            // Список упражнений
            FlowRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.exercises_point_title),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.exercises,
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
        }
    }
}

@PreviewLightDark
@Composable
fun ChooseWorkoutScreenPreview() {
    SportBuddyTheme {
        ChooseWorkoutScreenContent(
            items = persistentListOf(
                WorkoutListItem(
                    id = "12312",
                    name = "Грудь + трицепс",
                    muscleGroups = "Грудь, Трицепс, Предплечье",
                    exercises = "Тяга штанги к поясу, "
                ),
                WorkoutListItem(
                    id = "123132",
                    name = "Спина + бицепс",
                    muscleGroups = "Широчайшие мышцы, Спина, Предплечье",
                    exercises = "Тяга штанги к поясу, Подтягивания широким хватом, Подъем гантели на бицепс (обычный), Подъем гантели молот"
                )
            ),
            onItemClick = {},
            onBackButtonClick = {},
        )
    }
}