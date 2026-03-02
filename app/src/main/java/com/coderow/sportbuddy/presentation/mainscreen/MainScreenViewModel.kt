package com.coderow.sportbuddy.presentation.mainscreen

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.presentation.mainscreen.model.MainMenuItem
import com.coderow.sportbuddy.presentation.mainscreen.model.MainMenuItemType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class MainScreenViewModel : BaseViewModel() {

    val menuItemsFlow: StateFlow<ImmutableList<MainMenuItem>> = flow {
        val menuList = persistentListOf(
            MainMenuItem(
                itemType = MainMenuItemType.START_TRAINING,
            ),
            MainMenuItem(
                itemType = MainMenuItemType.CREATE_TRAINING,
            ),
            MainMenuItem(
                itemType = MainMenuItemType.EXERCISES_LIST,
            ),
            MainMenuItem(
                itemType = MainMenuItemType.CREATE_EXERCISE,
            ),
            MainMenuItem(
                itemType = MainMenuItemType.TRAINING_HISTORY,
            )
        )
        emit(menuList)
    }.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())
}