package com.coderow.sportbuddy.core.utils

import kotlinx.coroutines.CopyableThreadContextElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.coroutines.CoroutineContext

/**
 * Создает и сохраняет [StackTraceRecoveryException] во время запуска coroutine
 */
@OptIn(DelicateCoroutinesApi::class)
class StackTraceRecoveryElement internal constructor(
    private val state: Nesting,
) : CopyableThreadContextElement<Unit> {

    override val key: CoroutineContext.Key<*> = Key

    val exception: StackTraceRecoveryException? = if (state == Nesting.ROOT) StackTraceRecoveryException() else null

    override fun copyForChild(): CopyableThreadContextElement<Unit> = StackTraceRecoveryElement(state.next)

    override fun mergeForChild(overwritingElement: CoroutineContext.Element): CoroutineContext = copyForChild()

    override fun updateThreadContext(context: CoroutineContext) = Unit

    override fun restoreThreadContext(
        context: CoroutineContext,
        oldState: Unit
    )  = Unit

    companion object Key : CoroutineContext.Key<StackTraceRecoveryElement>

    enum class Nesting {
        SCOPE,
        ROOT,
        CHILD,
        ;

        val next: Nesting
            get() = when (this) {
                SCOPE -> ROOT
                ROOT -> CHILD
                CHILD -> CHILD
            }
    }
}

class StackTraceRecoveryException : RuntimeException("Details about the original stack trace: ")

fun createScopeRecoveryElement() = StackTraceRecoveryElement(StackTraceRecoveryElement.Nesting.SCOPE)