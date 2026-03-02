package com.coderow.sportbuddy.presentation.mainscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coderow.sportbuddy.R
import com.coderow.sportbuddy.core.presentation.cardShape
import com.coderow.sportbuddy.presentation.mainscreen.model.MainMenuItemType
import com.coderow.sportbuddy.presentation.ui.theme.LightPurple
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme

@Composable
fun MainMenuScreen() {
    val gridState = rememberLazyGridState()
    val viewModel = viewModel<MainScreenViewModel>()
    val menuItems by viewModel.menuItemsFlow.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPurple)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                style = typography.displayMedium,
                text = stringResource(R.string.app_name),
                textAlign = TextAlign.Center,
                color = Color.White,
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = menuItems) { item ->
                    MenuItem(item.itemType)
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    type: MainMenuItemType,
) {
    val title = when (type) {
        MainMenuItemType.CREATE_EXERCISE -> stringResource(R.string.create_exercise_title)
        MainMenuItemType.CREATE_TRAINING -> stringResource(R.string.create_training_title)
        MainMenuItemType.START_TRAINING -> stringResource(R.string.start_training_title)
        MainMenuItemType.EXERCISES_LIST -> stringResource(R.string.exercises_list_title)
        MainMenuItemType.TRAINING_HISTORY -> stringResource(R.string.training_history)
    }

    val iconRes = when (type) {
        MainMenuItemType.CREATE_EXERCISE -> R.drawable.dumbbell
        MainMenuItemType.CREATE_TRAINING -> R.drawable.sketchbook
        MainMenuItemType.START_TRAINING -> R.drawable.weightlifting
        MainMenuItemType.EXERCISES_LIST -> R.drawable.exercises_list
        MainMenuItemType.TRAINING_HISTORY -> R.drawable.exercises_history
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(80.dp, 80.dp),
                painter = painterResource(iconRes),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = title,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDark
@Composable
fun MainScreenPreview() {
    SportBuddyTheme {
        MainMenuScreen()
    }
}