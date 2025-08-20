package com.softklass.lazuli.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.theme.TopAppBarIcon

@Composable
fun ItemViewScreen(
    itemId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: ItemViewViewModel = hiltViewModel(),
) {
    val item by viewModel.item.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = onBack,
                includeBackArrow = true,
                title = {
                    Text(text = item?.content ?: "Item")
                },
                actions = {
                    TopAppBarIcon(icon = Icons.Rounded.EditNote) { item?.let { onEdit(it.id) } }
                },
                isEnabled = true,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets =
                    WindowInsets(
                        left = 8.dp,
                        top = 0.dp,
                        right = 0.dp,
                        bottom = 0.dp,
                    ),
                actions = {},
            )
        },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Notes",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            )
            // Notes Card
            val notes = item?.notes.orEmpty()

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                ) {
                    // Notes
                    if (notes.isBlank()) {
                        Text(
                            text = "No notes.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    } else {
                        if (item?.notesIsMarkdown == true) {
                            MarkdownBlock(text = notes)
                        } else {
                            Text(
                                text = notes,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }

            // Drawing section
            val drawingJson = item?.drawing
            if (!drawingJson.isNullOrBlank()) {
                Text(
                    text = "Drawing",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                )
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    DrawingPreview(
                        drawingJson = drawingJson,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawingPreview(
    drawingJson: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 4f,
) {
    val strokes = remember(drawingJson) { parseDrawingJson(drawingJson) }
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 8.dp),
    ) {
        // Compute bounds of all points
        val allPoints = strokes.flatten()
        if (allPoints.isEmpty()) return@Canvas
        val minX = allPoints.minOf { it.first }
        val maxX = allPoints.maxOf { it.first }
        val minY = allPoints.minOf { it.second }
        val maxY = allPoints.maxOf { it.second }

        val boundsWidth = (maxX - minX).coerceAtLeast(1e-3f)
        val boundsHeight = (maxY - minY).coerceAtLeast(1e-3f)

        // Leave a small margin around the drawing
        val margin = 8.dp.toPx()
        val availableWidth = (size.width - margin * 2).coerceAtLeast(1f)
        val availableHeight = (size.height - margin * 2).coerceAtLeast(1f)

        // Uniform scale to fit
        val scale = minOf(availableWidth / boundsWidth, availableHeight / boundsHeight)

        // Center the content
        val contentWidth = boundsWidth * scale
        val contentHeight = boundsHeight * scale
        val offsetX = (size.width - contentWidth) / 2f - minX * scale
        val offsetY = (size.height - contentHeight) / 2f - minY * scale

        for (stroke in strokes) {
            if (stroke.size < 2) continue
            val path =
                androidx.compose.ui.graphics
                    .Path()
            val first = stroke.first()
            path.moveTo(first.first * scale + offsetX, first.second * scale + offsetY)
            for (p in stroke.drop(1)) {
                path.lineTo(p.first * scale + offsetX, p.second * scale + offsetY)
            }
            drawPath(
                path = path,
                color = color,
                style =
                    androidx.compose.ui.graphics.drawscope
                        .Stroke(width = strokeWidth),
            )
        }
    }
}

private fun parseDrawingJson(json: String): List<List<Pair<Float, Float>>> {
    val out = mutableListOf<List<Pair<Float, Float>>>()
    try {
        val arr = org.json.JSONArray(json)
        for (i in 0 until arr.length()) {
            val sArr = arr.getJSONArray(i)
            val stroke = mutableListOf<Pair<Float, Float>>()
            for (j in 0 until sArr.length()) {
                val o = sArr.getJSONObject(j)
                val x = o.optDouble("x").toFloat()
                val y = o.optDouble("y").toFloat()
                stroke.add(x to y)
            }
            out.add(stroke)
        }
    } catch (_: Throwable) {
        // ignore
    }
    return out
}

@Composable
fun MarkdownBlock(
    text: String,
) {
    val parser = remember { CommonmarkAstNodeParser() }
    val textAsNote = remember(parser, text) { parser.parse(text) }

    BasicRichText(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        RichTextThemeProvider(
            contentColorProvider = { MaterialTheme.colorScheme.onSurface },
        ) {
            BasicMarkdown(astNode = textAsNote)
        }
    }
}
