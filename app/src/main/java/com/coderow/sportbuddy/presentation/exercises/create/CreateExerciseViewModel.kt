package com.coderow.sportbuddy.presentation.exercises.create

import com.coderow.sportbuddy.core.presentation.BaseViewModel
import com.coderow.sportbuddy.core.utils.CommandFlow
import com.coderow.sportbuddy.core.utils.emit
import com.coderow.sportbuddy.data.repository.ExerciseRepository
import com.coderow.sportbuddy.domain.Exercise
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.presentation.exercises.create.model.CreateExerciseCommand
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import com.coderow.sportbuddy.presentation.exercises.create.model.InventoryItem
import com.coderow.sportbuddy.presentation.exercises.create.model.MuscleGroupItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class CreateExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
): BaseViewModel() {

    val commandFlow = CommandFlow<CreateExerciseCommand>(viewModelScope)

    private val _muscleGroupFlow = MutableStateFlow(initialMuscleItems())
    val muscleGroupsFlow: StateFlow<ImmutableSet<MuscleGroupItem>> = _muscleGroupFlow.asStateFlow()

    private val _exerciseName = MutableStateFlow("")
    val exerciseName: StateFlow<String> = _exerciseName.asStateFlow()

    private val _inventoryWeight = MutableStateFlow("")
    val inventoryWeight: StateFlow<String> = _inventoryWeight.asStateFlow()

    private val _inventoryItemsFlow = MutableStateFlow(initialInventoryItems())
    val inventoryItemsFlow: StateFlow<ImmutableList<InventoryItem>> = _inventoryItemsFlow.asStateFlow()

    private val _isSaveButtonEnabled = MutableStateFlow(false)
    val isSaveButtonEnabled: StateFlow<Boolean> = _isSaveButtonEnabled.asStateFlow()

    private fun initialMuscleItems(): ImmutableSet<MuscleGroupItem> {
        return MuscleGroupType.entries.map { muscleGroup ->
            MuscleGroupItem(
                muscleGroup = muscleGroup,
                isSelected = false,
            )
        }.toPersistentSet()
    }

    private fun initialInventoryItems(): ImmutableList<InventoryItem> {
        return ExerciseInventoryType.entries.map { inventoryType ->
            InventoryItem(
                type = inventoryType,
                isSelected = false,
            )
        }.toImmutableList()
    }

    fun updateExerciseName(newName: String) {
        viewModelScope.launch {
            _exerciseName.update { newName }
            updateSaveButtonState()
        }
    }

    fun updateInventoryWeight(weight: String) {
        viewModelScope.launch {
            _inventoryWeight.update { weight }
            updateSaveButtonState()
        }
    }

    fun updateMuscleGroupSelected(item: MuscleGroupItem, isSelected: Boolean) {
        viewModelScope.launch {
            _muscleGroupFlow.update { currentData ->
                currentData.map { muscleGroup ->
                    if (item.muscleGroup == muscleGroup.muscleGroup) {
                        muscleGroup.copy(isSelected = isSelected)
                    } else {
                        muscleGroup
                    }
                }.toImmutableSet()
            }
            updateSaveButtonState()
        }
    }

    fun updateInventoryTypeSelected(item: InventoryItem) {
        viewModelScope.launch {
            _inventoryItemsFlow.update { currentData ->
                currentData.map { inventoryItem ->
                    inventoryItem.copy(isSelected = inventoryItem.type == item.type)
                }.toImmutableList()
            }
            updateSaveButtonState()
        }
    }

    private fun updateSaveButtonState() {
        _isSaveButtonEnabled.update { isSaveButtonEnabled() }
    }

    private fun isSaveButtonEnabled(): Boolean {
        val muscleGroups = muscleGroupsFlow.value.map { it.muscleGroup }.toSet()
        val inventoryType = inventoryItemsFlow.value.firstOrNull { it.isSelected }?.type
        val inventoryWeight = inventoryWeight.value
        val exerciseName = exerciseName.value

        return muscleGroups.isNotEmpty()
                && inventoryType != null
                && exerciseName.isNotBlank()
    }

    fun onSaveExerciseClick() {
        viewModelScope.launch {
            val muscleGroups = muscleGroupsFlow.value.map { it.muscleGroup }.toSet()
            val inventoryType = inventoryItemsFlow.value.firstOrNull { it.isSelected }?.type
            val inventoryWeight = if (inventoryWeight.value.isNotBlank()) {
                inventoryWeight.value.toDouble()
            } else {
                null
            }

            val exercise = Exercise(
                id = UUID.randomUUID().toString(),
                name = exerciseName.value,
                muscleGroups = muscleGroups,
                inventoryType = inventoryType,
                inventoryWeight = inventoryWeight,
            )

            exerciseRepository.insertExercise(exercise)
            commandFlow emit CreateExerciseCommand.ExitScreen
        }
    }
}