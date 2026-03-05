package com.coderow.sportbuddy.core.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coderow.sportbuddy.core.utils.CoroutineExceptionHandlerHolder
import com.coderow.sportbuddy.core.utils.createScopeRecoveryElement
import kotlinx.coroutines.plus

fun ViewModel.createViewModelScope() = CoroutineExceptionHandlerHolder.uncaughtExceptionHandler?.let { handler ->
    viewModelScope + handler + createScopeRecoveryElement()
} ?: viewModelScope

internal val cardShape = RoundedCornerShape(12.dp)
internal val roundButtonShape = RoundedCornerShape(24.dp)