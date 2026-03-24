package com.coderow.sportbuddy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.coderow.sportbuddy.data.model.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutTemplateEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutTemplateEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutTemplateEntity)

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getWorkoutById(id: String): WorkoutTemplateEntity?

    @Query("SELECT * FROM workout_templates")
    fun observeAllWorkouts(): Flow<List<WorkoutTemplateEntity>>

    @Query("DELETE FROM workout_templates WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)
}