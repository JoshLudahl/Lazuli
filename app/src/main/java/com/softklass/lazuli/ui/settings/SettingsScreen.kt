package com.softklass.lazuli.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.BuildConfig
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.theme.ThemeManager
import com.softklass.lazuli.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = onBack,
                includeBackArrow = true,
                title = {
                    Text("Settings")
                },
                actions = {},
                isEnabled = true,
            )
        },
        modifier = Modifier.padding(8.dp),
    ) { innerPadding ->
        SettingsContent(
            modifier = Modifier.padding(innerPadding),
            context = context,
        )
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    context: Context,
) {
    val themeManager = ThemeManager.getInstance()
    val currentThemeMode by themeManager.themeMode.collectAsState()
    val currentDynamicColor by themeManager.dynamicColor.collectAsState()

    val selectedColorOption = if (currentDynamicColor) 1 else 0
    val selectedThemeOption = currentThemeMode.ordinal

    val colorOptions = listOf("Default", "Dynamic")
    val themeOptions = listOf("Light", "Dark", "System")

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Color Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Color Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    colorOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = colorOptions.size),
                            onClick = { themeManager.setDynamicColor(index == 1) },
                            selected = index == selectedColorOption,
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        // Theme Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    themeOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
                            onClick = { themeManager.setThemeMode(ThemeMode.values()[index]) },
                            selected = index == selectedThemeOption,
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // PlayStore Review Button
        Button(
            onClick = { openPlayStoreReview(context) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Leave a Review")
        }

        // App Version
        Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

private fun openPlayStoreReview(context: Context) {
    try {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
                setPackage("com.android.vending")
            }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to web browser if Play Store app is not available
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
        context.startActivity(intent)
    }
}
