package com.coderow.sportbuddy.presentation.exercises.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.cardShape
import com.coderow.sportbuddy.core.presentation.roundButtonShape
import com.coderow.sportbuddy.core.utils.clickableWithDebounceAndSoundEffect
import com.coderow.sportbuddy.presentation.ui.compose.ScreenTopAppBar
import com.coderow.sportbuddy.presentation.ui.theme.LightPurple
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.coderow.sportbuddy.core.utils.observe
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.presentation.exercises.create.model.CreateExerciseCommand
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import com.coderow.sportbuddy.presentation.exercises.create.model.InventoryItem
import com.coderow.sportbuddy.presentation.exercises.create.model.MuscleGroupItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

@Composable
internal fun CreateExerciseScreen(
    navController: NavController,
    viewModel: CreateExerciseViewModel = hiltViewModel(),
) {

    val exerciseName by viewModel.exerciseName.collectAsStateWithLifecycle()
    val muscleGroupSet by viewModel.muscleGroupsFlow.collectAsStateWithLifecycle()
    val inventoryItems by viewModel.inventoryItemsFlow.collectAsStateWithLifecycle()
    val isSaveButtonEnabled by viewModel.isSaveButtonEnabled.collectAsStateWithLifecycle()

    viewModel.commandFlow.observe { command ->
        when (command) {
            is CreateExerciseCommand.ExitScreen -> navController.popBackStack()
        }
    }

    CreateExerciseContent(
        exerciseName = exerciseName,
        muscleGroupSet = muscleGroupSet,
        inventoryItems = inventoryItems,
        isSaveButtonEnabled = isSaveButtonEnabled,
        onBackButtonClick = { navController.popBackStack() },
        onNameInput = { viewModel.updateExerciseName(it) },
        onMuscleGroupClick = { group, isSelected ->
            viewModel.updateMuscleGroupSelected(group, isSelected)
        },
        onInventoryTypeClick = { item ->
            viewModel.updateInventoryTypeSelected(item)
        },
        onSaveExerciseClick = { viewModel.onSaveExerciseClick() },
    )
}

@Composable
private fun CreateExerciseContent(
    exerciseName: String,
    muscleGroupSet: ImmutableSet<MuscleGroupItem>,
    inventoryItems: ImmutableList<InventoryItem>,
    isSaveButtonEnabled: Boolean,
    onBackButtonClick: () -> Unit,
    onNameInput: (String) -> Unit,
    onMuscleGroupClick: (group: MuscleGroupItem, isSelected: Boolean) -> Unit,
    onInventoryTypeClick: (InventoryItem) -> Unit,
    onSaveExerciseClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenTopAppBar(
                title = stringResource(R.string.create_exercise_title),
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
                item(
                    key = "description",
                ) {
                    ExerciseDescriptionBlock(
                        name = exerciseName,
                        onNameInput = { onNameInput(it) }
                    )
                }

                item(
                    key = "muscleGroupsBlock",
                ) {
                    MuscleGroupsBlock(
                        muscleGroupSet = muscleGroupSet,
                        onMuscleGroupClick = { group, isSelected ->
                            onMuscleGroupClick(group, isSelected)
                        }
                    )
                }

                item(
                    key = "inventoryBlock",
                ) {
                    InventoryBlock(
                        inventoryItems = inventoryItems,
                        onItemClick = { item ->
                            onInventoryTypeClick(item)
                        }
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
                        onSaveExerciseClick()
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
private fun ExerciseDescriptionBlock(
    name: String,
    onNameInput: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = typography.headlineSmall,
            text = stringResource(R.string.exercise_description),
            color = Color.Black,
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            value = name,
            onValueChange = { onNameInput(it) },
            placeholder = {
                Text(
                    text = stringResource(R.string.exercise_name_text),
                    style = typography.titleMedium,
                )
            },
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
}

@Composable
private fun MuscleGroupsBlock(
    muscleGroupSet: ImmutableSet<MuscleGroupItem>,
    onMuscleGroupClick: (group: MuscleGroupItem, isSelected: Boolean) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = typography.headlineSmall,
            text = stringResource(R.string.exercise_muscles_involved),
            color = Color.Black,
        )

        FlowRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            muscleGroupSet.forEach { group ->
                val isSelected = group.isSelected

                FilterChip(
                    selected = group.isSelected,
                    onClick = {
                        onMuscleGroupClick(group, !isSelected)
                    },
                    label = {
                        Text(
                            text = group.muscleGroup.value,
                            color = Color.Black,
                            style = typography.titleSmall
                        )
                    },
                    leadingIcon = if (group.isSelected) {
                        {
                            Icon(
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun InventoryBlock(
    inventoryItems: ImmutableList<InventoryItem>,
    onItemClick: (InventoryItem) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = typography.headlineSmall,
            text = stringResource(R.string.exercise_inventory),
            color = Color.Black,
        )

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            inventoryItems.forEach { item ->
                InventoryCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    item = item,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@Composable
private fun InventoryCard(
    modifier: Modifier = Modifier,
    item: InventoryItem,
    onItemClick: (InventoryItem) -> Unit,
) {
    val title = item.type.value

    val borderColor = animateColorAsState(
        targetValue = if (item.isSelected) Color.Green else Color.DarkGray,
        label = "borderColorAnimation"
    )

    val iconRes = when (item.type) {
        ExerciseInventoryType.DUMBBELL -> R.drawable.dumbbell_inventory
        ExerciseInventoryType.BARBELL -> R.drawable.barbell
        ExerciseInventoryType.HORIZONTAL_BAR -> R.drawable.horizontal_bar
        ExerciseInventoryType.SELF_WEIGHT -> R.drawable.sportsman
    }

    Box(
        modifier = modifier
            .clip(cardShape)
            .background(Color.White)
            .border(
                border = BorderStroke(4.dp, borderColor.value),
                shape = cardShape
            )
            .clickableWithDebounceAndSoundEffect {
                onItemClick(item)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(iconRes),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = title,
                color = Color.Black,
                style = typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDark
@Composable
fun MainScreenPreview() {
    SportBuddyTheme {
        CreateExerciseContent(
            exerciseName = "",
            muscleGroupSet = MuscleGroupType.entries.map { muscleGroup ->
                MuscleGroupItem(
                    muscleGroup = muscleGroup,
                    isSelected = false,
                )
            }.toPersistentSet(),
            inventoryItems = ExerciseInventoryType.entries.map { inventoryType ->
                InventoryItem(
                    type = inventoryType,
                    isSelected = false,
                )
            }.toPersistentList(),
            isSaveButtonEnabled = true,
            onBackButtonClick = {},
            onNameInput = {},
            onMuscleGroupClick = { _, _ -> },
            onInventoryTypeClick = {},
            onSaveExerciseClick = {},
        )
    }
}