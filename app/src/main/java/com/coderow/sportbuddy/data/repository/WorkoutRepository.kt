package com.coderow.sportbuddy.data.repository

import com.coderow.sportbuddy.data.dao.WorkoutDao
import com.coderow.sportbuddy.data.model.WorkoutTemplateEntity
import com.coderow.sportbuddy.domain.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    suspend fun insertWorkout(workout: WorkoutTemplate) {
        workoutDao.insertWorkout(WorkoutTemplateEntity.fromDomain(workout))
    }

    suspend fun updateWorkout(workout: WorkoutTemplate) {
        workoutDao.updateWorkout(WorkoutTemplateEntity.fromDomain(workout))
    }

    suspend fun deleteWorkout(workout: WorkoutTemplate) {
        workoutDao.deleteWorkout(WorkoutTemplateEntity.fromDomain(workout))
    }

    suspend fun deleteWorkoutById(id: String) {
        workoutDao.deleteWorkoutById(id)
    }

    suspend fun getWorkoutById(id: String): WorkoutTemplate? {
        return workoutDao.getWorkoutById(id)?.toDomain()
    }

    fun observeAllWorkouts(): Flow<List<WorkoutTemplate>> {
        return workoutDao.observeAllWorkouts().map { list ->
            list.map { it.toDomain() }
        }
    }
}