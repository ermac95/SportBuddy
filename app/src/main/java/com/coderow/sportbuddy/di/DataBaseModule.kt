package com.coderow.sportbuddy.di

import android.content.Context
import androidx.room.Room
import com.coderow.sportbuddy.core.database.AppDatabase
import com.coderow.sportbuddy.data.converters.ExerciseTypeConverters
import com.coderow.sportbuddy.data.converters.WorkoutTypeConverters
import com.coderow.sportbuddy.data.dao.ExerciseDao
import com.coderow.sportbuddy.data.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        exerciseTypeConverters: ExerciseTypeConverters,
        workoutTypeConverters: WorkoutTypeConverters,
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app_database"
        )
            .fallbackToDestructiveMigration(true)
            .addTypeConverter(exerciseTypeConverters)
            .addTypeConverter(workoutTypeConverters)
            .build()
    }

    @Provides
    @Singleton
    fun provideExerciseDao(
        appDatabase: AppDatabase
    ): ExerciseDao {
        return appDatabase.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(
        appDatabase: AppDatabase
    ): WorkoutDao {
        return appDatabase.workoutDao()
    }
}