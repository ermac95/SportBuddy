package com.coderow.sportbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.coderow.sportbuddy.data.converters.ExerciseTypeConverters
import com.coderow.sportbuddy.domain.Exercise
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType

@Entity(tableName = "exercises")
@TypeConverters(ExerciseTypeConverters::class)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val muscleGroups: Set<MuscleGroupType>,
    val inventoryType: ExerciseInventoryType?,
    val inventoryWeight: Double?,
) {
    // Convert to domain model
    fun toDomain(): Exercise = Exercise(
        id = id,
        name = name,
        muscleGroups = muscleGroups,
        inventoryType = inventoryType,
        inventoryWeight = inventoryWeight
    )

    companion object {
        // Create from domain model
        fun fromDomain(exercise: Exercise): ExerciseEntity = ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            muscleGroups = exercise.muscleGroups,
            inventoryType = exercise.inventoryType,
            inventoryWeight = exercise.inventoryWeight
        )
    }
}