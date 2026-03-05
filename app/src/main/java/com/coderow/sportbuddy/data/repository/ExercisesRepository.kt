package com.coderow.sportbuddy.data.repository

import com.coderow.sportbuddy.data.dao.ExerciseDao
import com.coderow.sportbuddy.data.model.ExerciseEntity
import com.coderow.sportbuddy.domain.Exercise
import com.coderow.sportbuddy.domain.MuscleGroupType
import com.coderow.sportbuddy.presentation.exercises.create.model.ExerciseInventoryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(ExerciseEntity.fromDomain(exercise))
    }

    suspend fun insertAllExercises(exercises: List<Exercise>) {
        exerciseDao.insertAllExercises(exercises.map { ExerciseEntity.fromDomain(it) })
    }

    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(ExerciseEntity.fromDomain(exercise))
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(ExerciseEntity.fromDomain(exercise))
    }

    suspend fun deleteExerciseById(exerciseId: String) {
        exerciseDao.deleteExerciseById(exerciseId)
    }

    suspend fun getExerciseById(exerciseId: String): Exercise? {
        return exerciseDao.getExerciseById(exerciseId)?.toDomain()
    }

    fun observeExerciseById(exerciseId: String): Flow<Exercise?> {
        return exerciseDao.observeExerciseById(exerciseId).map { it?.toDomain() }
    }

    suspend fun getAllExercises(): List<Exercise> {
        return exerciseDao.getAllExercises().map { it.toDomain() }
    }

    fun observeAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.observeAllExercises().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getExercisesByMuscleGroup(muscleGroup: MuscleGroupType): List<Exercise> {
        return exerciseDao.getExercisesByMuscleGroup(muscleGroup.name)
            .map { it.toDomain() }
    }

    suspend fun getExercisesByInventoryType(inventoryType: ExerciseInventoryType): List<Exercise> {
        return exerciseDao.getExercisesByInventoryType(inventoryType.name)
            .map { it.toDomain() }
    }
}