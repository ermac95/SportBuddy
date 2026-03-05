package com.coderow.sportbuddy.presentation.exercises.list

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.data.repository.ExerciseRepository
import com.coderow.sportbuddy.domain.Exercise
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import com.coderow.sportbuddy.presentation.exercises.list.model.ExerciseListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExercisesListViewModel @Inject constructor(
    repository: ExerciseRepository,
) : BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercisesListFlow: StateFlow<ImmutableList<ExerciseListItem>> = repository.observeAllExercises().mapLatest { exercises ->
        val items = exercises.map { it.toUiModel() }
        items.toImmutableList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    private fun Exercise.toUiModel(): ExerciseListItem =
        ExerciseListItem(
            name = name,
            muscleGroups = muscleGroups.joinToString(", ") { it.value },
            inventoryType = inventoryType ?: ExerciseInventoryType.SELF_WEIGHT,
            inventoryWeight = inventoryWeight.toString(),
        )
}