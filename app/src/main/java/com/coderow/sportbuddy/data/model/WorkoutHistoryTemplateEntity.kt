package com.coderow.sportbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.coderow.sportbuddy.data.converters.WorkoutHistoryTypeConverters
import com.coderow.sportbuddy.data.converters.WorkoutTypeConverters
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import com.coderow.sportbuddy.domain.WorkoutHistoryTemplate
import java.util.Date

@Entity(tableName = "workout_history_templates")
@TypeConverters(WorkoutTypeConverters::class, WorkoutHistoryTypeConverters::class)
data class WorkoutHistoryTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val date: Long,
    val intervalBetweenExercises: TimeInterval,
    val exercisesList: List<WorkoutExerciseHistoryTemplate>,
) {
    fun toDomain(): WorkoutHistoryTemplate = WorkoutHistoryTemplate(
        id = id,
        name = name,
        date = Date(date),
        intervalBetweenExercises = intervalBetweenExercises,
        exercisesList = exercisesList,
    )

    companion object {
        fun fromDomain(workoutTemplate: WorkoutHistoryTemplate): WorkoutHistoryTemplateEntity = WorkoutHistoryTemplateEntity(
            id = workoutTemplate.id,
            name = workoutTemplate.name,
            date = workoutTemplate.date.time,
            intervalBetweenExercises = workoutTemplate.intervalBetweenExercises,
            exercisesList = workoutTemplate.exercisesList,
        )
    }
}