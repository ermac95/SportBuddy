package com.coderow.sportbuddy.core.utils

import kotlinx.coroutines.CoroutineExceptionHandler

object CoroutineExceptionHandlerHolder {

    var uncaughtExceptionHandler: CoroutineExceptionHandler? = null
}