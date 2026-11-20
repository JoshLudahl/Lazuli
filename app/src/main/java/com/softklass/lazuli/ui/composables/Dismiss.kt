package com.softklass.lazuli.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun <T> SwipeToDismissContainer(
    item: T,
    onSwipeLeftToRight: (T) -> Unit,
    onSwipeRightToLeft: (T) -> Unit,
    leftIcon: ImageVector = Icons.Rounded.Edit,
    rightIcon: ImageVector = Icons.Rounded.Delete,
    content: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val dismissState =
        rememberSwipeToDismissBoxState(
            positionalThreshold = { distance -> distance * 0.25f },
        )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        onDismiss = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onSwipeLeftToRight(item)
                    coroutineScope.launch {
                        dismissState.reset()
                    }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeRightToLeft(item)
                    coroutineScope.launch {
                        dismissState.reset()
                    }
                }

                else -> {}
            }
        },
        backgroundContent = {
            DismissBackground(
                dismissState = dismissState,
                leftIcon = leftIcon,
                rightIcon = rightIcon,
            )
        },
    ) {
        content(item)
    }
}

@Composable
fun DismissBackground(
    dismissState: SwipeToDismissBoxState,
    leftIcon: ImageVector = Icons.Rounded.Edit,
    rightIcon: ImageVector = Icons.Rounded.Delete,
) {
    val direction = dismissState.dismissDirection

    val color =
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.secondaryContainer
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
            SwipeToDismissBoxValue.Settled -> Color.Transparent
        }

    val alignment =
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            SwipeToDismissBoxValue.Settled -> Alignment.Center
        }

    val icon =
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> leftIcon
            SwipeToDismissBoxValue.EndToStart -> rightIcon
            SwipeToDismissBoxValue.Settled -> null
        }

    val iconTint =
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onSecondaryContainer
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
            SwipeToDismissBoxValue.Settled -> Color.Transparent
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .background(color, shape = MaterialTheme.shapes.largeIncreased),
        contentAlignment = alignment,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = if (direction == SwipeToDismissBoxValue.EndToStart) "Delete" else "Edit",
                tint = iconTint,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
