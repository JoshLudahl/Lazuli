package com.softklass.lazuli.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableTopAppBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    title: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    includeBackArrow: Boolean = true,
    isEnabled: Boolean = true,
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            if (includeBackArrow) {
                FilledIconButton(
                    onClick = onNavigateBack,
                    enabled = isEnabled,
                    colors =
                        IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier,
                    )
                }
            }
        },
        actions = { actions() },
        modifier = modifier,
    )
}
