package com.coderow.sportbuddy.core.utils

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Stable

val gridItemSpan2 : (LazyGridItemSpanScope) -> GridItemSpan = @Stable {
    GridItemSpan(2)
}

val gridItemMaxLineSpan: LazyGridItemSpanScope.() -> GridItemSpan = @Stable {
    GridItemSpan(currentLineSpan = maxLineSpan)
}