package com.coderow.sportbuddy.core.utils

import android.view.SoundEffectConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * Math.PI
        val x = cos(angleRad).toFloat()
        val y = sin(angleRad).toFloat()

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ),
            size = size
        )
    }
)

/**
 * Преобразование Modifier при условии condition
 */
@Stable
inline fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        modifier()
    } else {
        this
    }
}

inline fun Modifier.clickableWithDebounceAndSoundEffect(
    enabled: Boolean = true,
    soundConstant: Int = SoundEffectConstants.CLICK,
    debounceDelay: Duration = 0.5.seconds,
    role: Role? = null,
    indication: Indication? = null,
    forceNoIndication: Boolean = false,
    onClickLabel: String? = null,
    crossinline onClick: () -> Unit,
): Modifier = this.composed {
    Modifier.clickableWithSoundEffect(
        enabled = enabled,
        role = role,
        soundConstant = soundConstant,
        debounceDelay = debounceDelay,
        onClick = onClick,
        onClickLabel = onClickLabel,
        indication = if (forceNoIndication) null else indication ?: LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() },
    )
}

// Воркэраунд для воссоздания тактильного фидбека (звук клика по дифолту)
// до момента реализации поведения аналогичного во view
// https://issuetracker.google.com/issues/218064821
/**
 * Default clickable with sound effect.
 * If you DON'T NEED TO RIPPLE write `indication = null`.
 * By default, NO DEBOUNCE, prefer to use `Modifier.clickableWithDebounceAndSoundEffect`.
 */
@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.clickableWithSoundEffect(
    enabled: Boolean = true,
    soundConstant: Int = SoundEffectConstants.CLICK,
    debounceDelay: Duration = 0.seconds,
    role: Role? = null,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource,
    onClickLabel: String? = null,
    noinline onLongClick: (() -> Unit)? = null,
    crossinline onClick: () -> Unit,
): Modifier = this.composed {
    val lastClickTimestamp = remember { mutableLongStateOf(0L) }
    val view = LocalView.current
    combinedClickable(
        enabled = enabled,
        role = role,
        onClick = {
            onClickDebounce(
                debounceDelay = debounceDelay,
                soundConstant = soundConstant,
                lastClickTimestamp = lastClickTimestamp,
                onClick = onClick,
                view = view,
            )
        },
        onLongClick = onLongClick,
        onClickLabel = onClickLabel,
        interactionSource = interactionSource,
        indication = indication,
    )
}

