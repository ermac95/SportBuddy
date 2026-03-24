package com.coderow.sportbuddy.domain

import kotlinx.serialization.Serializable
import kotlin.time.Duration

/**
 * Данные по временному интервалу
 */
@Serializable
data class TimeInterval(
    val title: String,
    @Serializable(with = DurationSerializer::class)
    val duration: Duration,
)