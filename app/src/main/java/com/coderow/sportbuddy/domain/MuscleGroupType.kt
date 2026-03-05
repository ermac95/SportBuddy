package com.coderow.sportbuddy.domain

import kotlinx.serialization.Serializable

@Serializable
enum class MuscleGroupType(val value: String) {
    TRAPEZIUS("Трапеции"),
    FRONT_DELT("Передние дельты"),
    MIDDLE_DELT("Средние дельты"),
    REAR_DELT("Задние дельты"),
    UPPER_CHEST("Верхние грудные"),
    MIDDLE_CHEST("Средние грудные"),
    BOTTOM_CHEST("Нижние грудные"),
    BICEPS("Бицепс"),
    TRICEPS("Трицепс"),
    FOREARM("Предплечья"),
    ABC("Прямые мышцы живота"),
    OBLIQUES("Косые мышцы живота"),
    LATS("Широчайшие мышцы"),
    BACK("Спина"),
    ;

}