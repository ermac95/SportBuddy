package com.coderow.sportbuddy.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class ExerciseTypeConverters @Inject constructor() {

    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // Convert Set<MuscleGroupType> to String for storage
    @TypeConverter
    fun fromMuscleGroupSet(muscleGroups: Set<MuscleGroupType>): String {
        return json.encodeToString(muscleGroups.toList())
    }

    // Convert String back to Set<MuscleGroupType>
    @TypeConverter
    fun toMuscleGroupSet(data: String): Set<MuscleGroupType> {
        return json.decodeFromString<List<MuscleGroupType>>(data).toSet()
    }

    // Convert ExerciseInventoryType? to String for storage
    @TypeConverter
    fun fromInventoryType(type: ExerciseInventoryType?): String? {
        return type?.let { json.encodeToString(it) }
    }

    // Convert String back to ExerciseInventoryType?
    @TypeConverter
    fun toInventoryType(data: String?): ExerciseInventoryType? {
        return data?.let { json.decodeFromString(it) }
    }
}