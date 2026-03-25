package com.coderow.sportbuddy.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> flowOf(getValue: suspend () -> T): Flow<T> =
    flow {
        emit(getValue())
    }