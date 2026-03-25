package com.coderow.sportbuddy.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExerciseHistoryTemplate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class WorkoutHistoryTypeConverters @Inject constructor() {

    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromWorkoutExerciseList(exercises: List<WorkoutExerciseHistoryTemplate>): String {
        return json.encodeToString(exercises)
    }

    @TypeConverter
    fun toWorkoutExerciseList(data: String): List<WorkoutExerciseHistoryTemplate> {
        return json.decodeFromString(data)
    }
}