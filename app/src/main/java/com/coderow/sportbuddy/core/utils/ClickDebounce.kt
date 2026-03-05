package com.coderow.sportbuddy.core.utils

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

fun Modifier.clickDebounce(
    enabled: Boolean = true,
    debounceDelay: Duration = 0.5.seconds,
    onClick: () -> Unit,
): Modifier = composed {
    val view = LocalView.current
    val lastClickTimestamp = remember { mutableStateOf<TimeMark?>(null) }

    clickable(
        enabled = enabled,
    ) {
        val timeHasPassedAfterClick = lastClickTimestamp.value
        if (timeHasPassedAfterClick == null || timeHasPassedAfterClick.hasPassedNow()) {
            lastClickTimestamp.value = TimeSource.Monotonic.markNow() + debounceDelay
            // https://issuetracker.google.com/issues/218064821
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick()
        }
    }
}

/**
 * If you need `Modifier.clickable` use `Modifier.clickableWithSoundEffect`
 * or `Modifier.clickableWithDebounceAndSoundEffect`.
 * `onClickDebounce` is for cases when you CAN'T use 'Modifier.clickable' (pointerInput, etc).
 */
inline fun onClickDebounce(
    debounceDelay: Duration = 0.5.seconds,
    soundConstant: Int = SoundEffectConstants.CLICK,
    lastClickTimestamp: MutableLongState,
    crossinline onClick: () -> Unit,
    view: View,
) {
    val timeHasPassedAfterClick = System.currentTimeMillis() - lastClickTimestamp.longValue
    if (timeHasPassedAfterClick > debounceDelay.inWholeMilliseconds) {
        lastClickTimestamp.longValue = System.currentTimeMillis()
        // https://issuetracker.google.com/issues/218064821
        view.playSoundEffect(soundConstant)
        onClick()
    }
}

@Composable
fun rememberLastClickTimestamp(): MutableLongState = remember { mutableLongStateOf(0L) }

/**
 * Не позволяет нажимать на кнопку чаще, чем [debounceDelay].
 * Кроме того содержит `playSoundEffect`, который почему-то не реализован в стандартном Compose.
 *
 * Внимание! Добавление inline может вызывать лишние рекомпозиции, из-за чего в UI тестах ломается idling resource
 */
@Composable
fun rememberOnClickDebounce(
    debounceDelay: Duration = 0.5.seconds,
    onClick: () -> Unit,
): () -> Unit {
    val lastClickTimestamp = rememberLastClickTimestamp()
    val view = LocalView.current
    val onClickDebounced by rememberUpdatedState(
        newValue = {
            onClickDebounce(
                debounceDelay = debounceDelay,
                lastClickTimestamp = lastClickTimestamp,
                onClick = onClick,
                view = view
            )
        }
    )
    return onClickDebounced
}