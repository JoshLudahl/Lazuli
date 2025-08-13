package com.softklass.lazuli.ui.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.softklass.lazuli.BuildConfig
import com.softklass.lazuli.R
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.theme.ThemeManager
import com.softklass.lazuli.ui.theme.ThemeMode
import com.softklass.lazuli.ui.theme.TopAppBarIcon
import com.softklass.lazuli.utils.shareAppIntent

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
                actions = {
                    TopAppBarIcon { shareAppIntent(context) }
                },
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    colorOptions.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = index == selectedColorOption,
                            onCheckedChange = { if (it) themeManager.setDynamicColor(index == 1) },
                            modifier = Modifier.weight(1f),
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    colorOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                        ) {
                            if (selectedColorOption == index) {
                                Icon(
                                    Icons.Rounded.Done,
                                    contentDescription = "Localized description",
                                )
                            }

                            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))

                            Text(label, maxLines = 1)
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    themeOptions.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = index == selectedThemeOption,
                            onCheckedChange = { if (it) themeManager.setThemeMode(ThemeMode.entries[index]) },
                            modifier = Modifier.weight(1.2f),
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    themeOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                        ) {
                            if (selectedThemeOption == index) {
                                Icon(
                                    Icons.Rounded.Done,
                                    contentDescription = "Localized description",
                                )
                            }

                            Text(label, maxLines = 1)
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "About",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Lazuli Bunting",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Lazuli is inspired after the lazuli bunting, a small bird known for its vibrant blue color.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                AttributionAnnotatedText()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { openPlayStoreReview(context) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = "Leave a review icon",
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
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
                data = "market://details?id=${context.packageName}".toUri()
                setPackage("com.android.vending")
            }
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e("SettingsScreen", "Error opening Play Store", e)
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data =
                    "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
            }
        context.startActivity(intent)
    }
}

@Composable
fun AttributionAnnotatedText() {
    val annotatedLinkString: AnnotatedString =
        remember {
            buildAnnotatedString {
                val style = SpanStyle()

                val styleCenter =
                    SpanStyle(
                        color = Color(0xff64B5F6),
                        textDecoration = TextDecoration.Underline,
                    )

                withStyle(
                    style = style,
                ) {
                    append("To learn more, ")
                }

                withLink(LinkAnnotation.Url(url = "https://myodfw.com/wildlife-viewing/species/lazuli-bunting")) {
                    withStyle(
                        style = styleCenter,
                    ) {
                        append("click here")
                    }
                }

                withStyle(
                    style = style,
                ) {
                    append(".")
                }
            }
        }

    Column {
        Text(
            text = annotatedLinkString,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
