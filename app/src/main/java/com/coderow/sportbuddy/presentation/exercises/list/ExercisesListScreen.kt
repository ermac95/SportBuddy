package com.coderow.sportbuddy.presentation.exercises.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.cardShape
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import com.coderow.sportbuddy.presentation.exercises.list.model.ExerciseListItem
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

//TODO в будущем здесь будет фильтрация по типу оборудования, группам мышц
@Composable
internal fun ExercisesListScreen(
    navController: NavController,
    viewModel: ExercisesListViewModel = hiltViewModel(),
) {
    val exercisesItems by viewModel.exercisesListFlow.collectAsStateWithLifecycle()

    ExercisesListContent(
        items = exercisesItems,
        onBackButtonClick = { navController.popBackStack() }
    )
}

@Composable
private fun ExercisesListContent(
    items: ImmutableList<ExerciseListItem>,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.exercises_list_title),
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
                items(items) { exercise ->
                    ExerciseCard(exercise)
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    item: ExerciseListItem
) {
    val borderColor = Color.DarkGray

    val iconRes = when (item.inventoryType) {
        ExerciseInventoryType.DUMBBELL -> R.drawable.dumbbell_inventory
        ExerciseInventoryType.BARBELL -> R.drawable.barbell
        ExerciseInventoryType.HORIZONTAL_BAR -> R.drawable.horizontal_bar
        ExerciseInventoryType.SELF_WEIGHT -> R.drawable.sportsman
    }

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
            horizontalAlignment = Alignment.Start,
        ) {
            Image(
                modifier = Modifier.size(40.dp, 40.dp),
                painter = painterResource(iconRes),
                contentDescription = null
            )

            Row(
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

            Row(
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
fun MainScreenPreview() {
    SportBuddyTheme {
        ExercisesListContent(
            items = persistentListOf(
                ExerciseListItem(
                    name = "Подьем гантели на бицепс",
                    muscleGroups = "Бицепс, Предплечье",
                    inventoryType = ExerciseInventoryType.DUMBBELL,
                ),
                ExerciseListItem(
                    name = "Тяга штанги к поясу",
                    muscleGroups = "Широчайшие мышцы, Спина, Предплечье",
                    inventoryType = ExerciseInventoryType.BARBELL,
                )
            ),
            onBackButtonClick = {},
        )
    }
}