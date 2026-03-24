package com.coderow.sportbuddy.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.coderow.sportbuddy.domain.TimeInterval
import com.coderow.sportbuddy.domain.WorkoutExercise
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class WorkoutTypeConverters @Inject constructor() {

    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromTimeInterval(interval: TimeInterval): String {
        return json.encodeToString(interval)
    }

    @TypeConverter
    fun toTimeInterval(data: String): TimeInterval {
        return json.decodeFromString(data)
    }

    @TypeConverter
    fun fromWorkoutExerciseList(exercises: List<WorkoutExercise>): String {
        return json.encodeToString(exercises)
    }

    @TypeConverter
    fun toWorkoutExerciseList(data: String): List<WorkoutExercise> {
        return json.decodeFromString(data)
    }
}