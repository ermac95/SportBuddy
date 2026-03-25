package com.coderow.sportbuddy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.coderow.sportbuddy.data.model.WorkoutHistoryTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutHistoryTemplateEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutHistoryTemplateEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutHistoryTemplateEntity)

    @Query("SELECT * FROM workout_history_templates WHERE id = :id")
    suspend fun getWorkoutById(id: String): WorkoutHistoryTemplateEntity?

    @Query("SELECT * FROM workout_history_templates")
    fun observeAllWorkouts(): Flow<List<WorkoutHistoryTemplateEntity>>

    @Query("DELETE FROM workout_history_templates WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)
}