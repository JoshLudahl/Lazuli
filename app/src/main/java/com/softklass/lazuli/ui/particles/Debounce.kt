package com.softklass.lazuli.ui.particles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun <T> T.useDebounce(
    delayMillis: Long = 300L,
    // 1. couroutine scope
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onChange: (T) -> Unit,
): T {
    // 2. updating state
    val state by rememberUpdatedState(this)

    // 3. launching the side-effect handler
    DisposableEffect(state) {
        val job =
            coroutineScope.launch {
                delay(delayMillis)
                onChange(state)
            }
        onDispose {
            job.cancel()
        }
    }
    return state
}

// Use as:
//     var isEnabled by remember { mutableStateOf(true) }.useDebounce { Log.i("Debounce", "isEnabled: $it") }
