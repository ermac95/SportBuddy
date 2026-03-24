package com.coderow.sportbuddy.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coderow.sportbuddy.data.converters.ExerciseTypeConverters
import com.coderow.sportbuddy.data.converters.WorkoutTypeConverters
import com.coderow.sportbuddy.data.dao.ExerciseDao
import com.coderow.sportbuddy.data.dao.WorkoutDao
import com.coderow.sportbuddy.data.model.ExerciseEntity
import com.coderow.sportbuddy.data.model.WorkoutTemplateEntity
import javax.inject.Inject

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutTemplateEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    ExerciseTypeConverters::class,
    WorkoutTypeConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    // For Hilt to create database instance with callbacks
    class Callback @Inject constructor() : RoomDatabase.Callback()
}