package com.coderow.sportbuddy.core.presentation

import androidx.lifecycle.ViewModel

/**
 * Базовый класс для всех вью моделей
 */
abstract class BaseViewModel : ViewModel() {

    val viewModelScope = createViewModelScope()
}