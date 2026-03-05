package com.coderow.sportbuddy.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coderow.sportbuddy.data.converters.ExerciseTypeConverters
import com.coderow.sportbuddy.data.dao.ExerciseDao
import com.coderow.sportbuddy.data.model.ExerciseEntity
import javax.inject.Inject

@Database(
    entities = [ExerciseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ExerciseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    // For Hilt to create database instance with callbacks
    class Callback @Inject constructor() : RoomDatabase.Callback()
}