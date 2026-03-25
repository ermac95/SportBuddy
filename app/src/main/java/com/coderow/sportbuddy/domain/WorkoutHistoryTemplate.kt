package com.coderow.sportbuddy.domain

import java.util.Date
import java.util.UUID

/**
 * Модель для хранения инфо об истории тренировки
 *
 * @param id - уникальный ИД
 * @param name - название тренировки
 * @param date - дата проведения тренировки
 * @param intervalBetweenExercises - интервал между упражнениями (в секундах)
 * @param exercisesList - список выполненных в рамках тренировки упражнений
 */
data class WorkoutHistoryTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: Date,
    val intervalBetweenExercises: TimeInterval,
    val exercisesList: List<WorkoutExerciseHistoryTemplate>,
)
