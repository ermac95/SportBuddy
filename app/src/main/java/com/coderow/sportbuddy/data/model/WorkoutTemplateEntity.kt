package com.coderow.sportbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.coderow.sportbuddy.data.converters.WorkoutTypeConverters
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExercise
import com.coderow.sportbuddy.domain.WorkoutTemplate

@Entity(tableName = "workout_templates")
@TypeConverters(WorkoutTypeConverters::class)
data class WorkoutTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val intervalBetweenExercises: TimeInterval,
    val exercisesList: List<WorkoutExercise>,
) {
    fun toDomain(): WorkoutTemplate = WorkoutTemplate(
        id = id,
        name = name,
        intervalBetweenExercises = intervalBetweenExercises,
        exercisesList = exercisesList,
    )

    companion object {
        fun fromDomain(workoutTemplate: WorkoutTemplate): WorkoutTemplateEntity = WorkoutTemplateEntity(
            id = workoutTemplate.id,
            name = workoutTemplate.name,
            intervalBetweenExercises = workoutTemplate.intervalBetweenExercises,
            exercisesList = workoutTemplate.exercisesList,
        )
    }
}