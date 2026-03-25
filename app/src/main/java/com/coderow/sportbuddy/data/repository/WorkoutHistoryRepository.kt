package com.coderow.sportbuddy.data.repository

import com.coderow.sportbuddy.data.dao.WorkoutHistoryDao
import com.coderow.sportbuddy.data.model.WorkoutHistoryTemplateEntity
import com.coderow.sportbuddy.domain.WorkoutHistoryTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutHistoryRepository @Inject constructor(
    private val workoutHistoryDao: WorkoutHistoryDao,
) {
    suspend fun insertWorkout(workout: WorkoutHistoryTemplate) {
        workoutHistoryDao.insertWorkout(WorkoutHistoryTemplateEntity.fromDomain(workout))
    }

    suspend fun updateWorkout(workout: WorkoutHistoryTemplate) {
        workoutHistoryDao.updateWorkout(WorkoutHistoryTemplateEntity.fromDomain(workout))
    }

    suspend fun deleteWorkout(workout: WorkoutHistoryTemplate) {
        workoutHistoryDao.deleteWorkout(WorkoutHistoryTemplateEntity.fromDomain(workout))
    }

    suspend fun deleteWorkoutById(id: String) {
        workoutHistoryDao.deleteWorkoutById(id)
    }

    suspend fun getWorkoutById(id: String): WorkoutHistoryTemplate? {
        return workoutHistoryDao.getWorkoutById(id)?.toDomain()
    }

    fun observeAllWorkouts(): Flow<List<WorkoutHistoryTemplate>> {
        return workoutHistoryDao.observeAllWorkouts().map { list ->
            list.map { it.toDomain() }
        }
    }
}