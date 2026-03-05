package com.coderow.sportbuddy.core.presentation

import androidx.lifecycle.ViewModel

/**
 * Базовый класс для всех вью моделей
 */
open class BaseViewModel : ViewModel() {

    val viewModelScope = createViewModelScope()
}