package com.softklass.lazuli.ui.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AlarmOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.softklass.lazuli.data.device.ReminderScheduler
import com.softklass.lazuli.ui.composables.ReusableTopAppBar
import com.softklass.lazuli.ui.composables.useDebounce
import com.softklass.lazuli.ui.theme.primaryLight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

@Composable
fun ItemEditScreen(
    viewModel: ItemEditViewModel,
    onBack: () -> Unit,
    itemId: Int,
) {
    val item by viewModel.item.collectAsStateWithLifecycle()
    var title by rememberSaveable(item) { mutableStateOf(item?.content ?: "") }
    var notes by rememberSaveable(item) { mutableStateOf(if (viewModel.isParentFlag) "" else (item as? com.softklass.lazuli.data.models.Item)?.notes.orEmpty()) }
    var notesIsMarkdown by rememberSaveable(item) { mutableStateOf(if (viewModel.isParentFlag) false else (item as? com.softklass.lazuli.data.models.Item)?.notesIsMarkdown ?: false) }
    // Persisted drawing JSON if any
    val initialDrawingJson = (item as? com.softklass.lazuli.data.models.Item)?.drawing
    var reminderAt by rememberSaveable(item) { mutableStateOf((item as? com.softklass.lazuli.data.models.Item)?.reminderAt) }

    var isEnabled by remember { mutableStateOf(true) }.useDebounce {
        Log.i(
            "Debounce",
            "isEnabled: $it",
        )
    }

    val context = LocalContext.current
    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = {
                    isEnabled = false
                    onBack()
                },
                includeBackArrow = true,
                title = {
                    Text("Edit")
                },
                actions = {
                    if (!viewModel.isParentFlag) {
                        com.softklass.lazuli.ui.theme.TopAppBarIcon(
                            icon = if (reminderAt != null) Icons.Rounded.Alarm else Icons.Rounded.AlarmOff,
                        ) {
                            val now = Calendar.getInstance()
                            val c = if (reminderAt != null) Calendar.getInstance().apply { timeInMillis = reminderAt!! } else now
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val picked =
                                                Calendar.getInstance().apply {
                                                    set(Calendar.YEAR, year)
                                                    set(Calendar.MONTH, month)
                                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                    set(Calendar.MINUTE, minute)
                                                    set(Calendar.SECOND, 0)
                                                    set(Calendar.MILLISECOND, 0)
                                                }
                                            reminderAt = picked.timeInMillis
                                        },
                                        c.get(Calendar.HOUR_OF_DAY),
                                        c.get(Calendar.MINUTE),
                                        false,
                                    ).show()
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        }
                    }
                },
                isEnabled = isEnabled,
            )
        },
        modifier = Modifier.padding(8.dp),
    ) { innerPadding ->
        ItemDetailScreenContent(
            modifier = Modifier.padding(innerPadding).fillMaxHeight(),
            title = title,
            onTitleChange = { title = it },
            isParent = viewModel.isParentFlag,
            notes = notes,
            onNotesChange = { notes = it },
            notesIsMarkdown = notesIsMarkdown,
            onNotesModeChange = { notesIsMarkdown = it },
            initialDrawingJson = initialDrawingJson,
            reminderAt = if (viewModel.isParentFlag) null else reminderAt,
            onReminderChange = { reminderAt = it },
            onSaveClick = { drawingJson ->
                if (title.trim().isNotEmpty()) {
                    viewModel.saveItem(
                        title,
                        if (viewModel.isParentFlag) null else notes,
                        if (viewModel.isParentFlag) false else notesIsMarkdown,
                        drawing = if (viewModel.isParentFlag) null else drawingJson,
                        reminderAt = if (viewModel.isParentFlag) null else reminderAt,
                    )
                    // Schedule or cancel reminder based on current selection
                    if (!viewModel.isParentFlag) {
                        val id = (item as? com.softklass.lazuli.data.models.Item)?.id ?: itemId
                        if (reminderAt != null) {
                            ReminderScheduler.scheduleReminder(context, id, title, reminderAt!!)
                        } else {
                            ReminderScheduler.cancelReminder(context, id)
                        }
                    }
                    onBack()
                } else {
                    Toast
                        .makeText(
                            context,
                            "Please enter a valid title.",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            },
        )
    }
}

@Composable
fun ItemDetailScreenContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    isParent: Boolean,
    notes: String,
    onNotesChange: (String) -> Unit,
    notesIsMarkdown: Boolean,
    onNotesModeChange: (Boolean) -> Unit,
    initialDrawingJson: String?,
    reminderAt: Long?,
    onReminderChange: (Long?) -> Unit,
    onSaveClick: (String?) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Persist and continue drawing: strokes state shared across this screen
        val strokes = remember { mutableStateListOf<List<TimedPoint>>() }
        var drawingCleared by remember { mutableStateOf(false) }
        // Preload strokes from existing drawing JSON
        LaunchedEffect(initialDrawingJson) {
            if (!initialDrawingJson.isNullOrBlank() && strokes.isEmpty()) {
                try {
                    val loaded = jsonToStrokes(initialDrawingJson)
                    strokes.addAll(loaded)
                    drawingCleared = false
                } catch (_: Throwable) {
                    // ignore malformed payload
                }
            }
        }
        val produceDrawingJson: () -> String? = {
            if (isParent) {
                null
            } else if (strokes.isNotEmpty()) {
                strokesToJson(strokes)
            } else if (drawingCleared) {
                null
            } else {
                initialDrawingJson
            }
        }
        OutlinedTextField(
            label = { Text("Title") },
            value = title,
            onValueChange = onTitleChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        if (title.trim().isNotEmpty()) {
                            onSaveClick(produceDrawingJson())
                        }
                    },
                ),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryLight,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryLight,
                    unfocusedLabelColor = Color.Gray,
                ),
            singleLine = true,
            maxLines = 1,
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        )

        if (!isParent) {
            // Reminder selector
            ReminderRow(
                reminderAt = reminderAt,
                onPick = { millis -> onReminderChange(millis) },
                onClear = { onReminderChange(null) },
            )

            // Notes section and input mode toggle (Text / Markdown / Draw)
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            )

            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var isRecognizing by remember { mutableStateOf(false) }
            var isModelReady by remember { mutableStateOf(false) }

            // 0 = Text, 1 = Markdown, 2 = Draw
            var notesMode by rememberSaveable(notesIsMarkdown) { mutableStateOf(if (notesIsMarkdown) 1 else 0) }

            @OptIn(ExperimentalMaterial3ExpressiveApi::class)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                ToggleButton(
                    checked = notesMode == 0,
                    onCheckedChange = {
                        if (it) {
                            notesMode = 0
                            onNotesModeChange(false)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                ) {
                    Text("Text", maxLines = 1)
                }
                ToggleButton(
                    checked = notesMode == 1,
                    onCheckedChange = {
                        if (it) {
                            notesMode = 1
                            onNotesModeChange(true)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                ) {
                    Text("Markdown", maxLines = 1)
                }
                ToggleButton(
                    checked = notesMode == 2,
                    onCheckedChange = { if (it) notesMode = 2 },
                    modifier = Modifier.weight(1f),
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                ) {
                    Text("Draw", maxLines = 1)
                }
            }

            if (notesMode == 2) {
                // Ensure Digital Ink model is downloaded once when entering Draw mode
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    try {
                        withContext(Dispatchers.IO) {
                            ensureDigitalInkModelAvailable()
                        }
                        isModelReady = true
                    } catch (t: Throwable) {
                        isModelReady = false
                        Toast.makeText(context, "Failed to prepare handwriting model: ${t.message ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                    }
                }
                // Drawing canvas and controls
                DrawingCanvas(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                    strokes = strokes,
                    strokeWidth = 6f,
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    withContext(Dispatchers.Main) { isRecognizing = true }
                                    // Take a snapshot of strokes to avoid concurrent modifications while recognizing
                                    val snapshot = strokes.map { it.toList() }
                                    val recognized = withTimeout(30_000) { recognizeInkToText(snapshot) }
                                    withContext(Dispatchers.Main) {
                                        if (recognized.isBlank()) {
                                            Toast.makeText(context, "No text recognized", Toast.LENGTH_SHORT).show()
                                        } else {
                                            onNotesChange(if (notes.isBlank()) recognized else (notes + "\n" + recognized))
                                            // Clear canvas after successful recognition to avoid re-recognizing old strokes
                                            strokes.clear()
                                            drawingCleared = true
                                            // Switch back to Text mode to allow editing
                                            notesMode = 0
                                        }
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Recognition timed out. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (t: Throwable) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Recognition failed: ${t.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                                    }
                                } finally {
                                    withContext(Dispatchers.Main) { isRecognizing = false }
                                }
                            }
                        },
                        enabled = strokes.isNotEmpty() && !isRecognizing && isModelReady,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (isRecognizing) {
                            CircularProgressIndicator(modifier = Modifier.height(16.dp))
                            Spacer(Modifier.height(0.dp))
                            Text(" Recognizing…")
                        } else if (!isModelReady) {
                            Text("Preparing model…")
                        } else {
                            Text("Recognize")
                        }
                    }
                    Spacer(Modifier.weight(0.1f))
                    Button(
                        onClick = {
                            strokes.clear()
                            drawingCleared = true
                        },
                        enabled = strokes.isNotEmpty() && !isRecognizing,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    ) {
                        Text("Clear")
                    }
                }
            } else {
                OutlinedTextField(
                    label = { Text("Notes") },
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                            .weight(1f),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryLight,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = primaryLight,
                            unfocusedLabelColor = Color.Gray,
                        ),
                    singleLine = false,
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                )
            }
        }

        Button(
            onClick = { onSaveClick(produceDrawingJson()) },
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            enabled = title.isNotBlank(),
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun ReminderRow(
    reminderAt: Long?,
    onPick: (Long) -> Unit,
    onClear: () -> Unit,
) {
    // Icon moved to TopAppBar. Keep text and clear button here.
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (reminderAt != null) {
            Text(
                text = formatReminder(reminderAt),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.80f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(0.dp))
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) { Text("Clear") }
        }
    }
}

private fun formatReminder(epochMillis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val month = java.text.SimpleDateFormat("MMMM", java.util.Locale.getDefault()).format(cal.time)
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hourMinute = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(cal.time)
    return "$month ${ordinal(day)} at $hourMinute"
}

private fun ordinal(day: Int): String {
    if (day in 11..13) return "${day}th"
    return when (day % 10) {
        1 -> "${day}st"
        2 -> "${day}nd"
        3 -> "${day}rd"
        else -> "${day}th"
    }
}

// Simple data structure for a timed point used by Digital Ink
private data class TimedPoint(
    val x: Float,
    val y: Float,
    val t: Long,
)

// Serialize/deserialize strokes as JSON: [[{"x":..,"y":..,"t":..}, ...], ...]
private fun strokesToJson(strokes: List<List<TimedPoint>>): String {
    val arr = JSONArray()
    for (stroke in strokes) {
        val s = JSONArray()
        for (p in stroke) {
            val o = JSONObject()
            o.put("x", p.x)
            o.put("y", p.y)
            o.put("t", p.t)
            s.put(o)
        }
        arr.put(s)
    }
    return arr.toString()
}

private fun jsonToStrokes(json: String): List<List<TimedPoint>> {
    val result = mutableListOf<List<TimedPoint>>()
    val arr = JSONArray(json)
    for (i in 0 until arr.length()) {
        val sArr = arr.getJSONArray(i)
        val stroke = mutableListOf<TimedPoint>()
        for (j in 0 until sArr.length()) {
            val o = sArr.getJSONObject(j)
            val x = o.optDouble("x").toFloat()
            val y = o.optDouble("y").toFloat()
            val t = o.optLong("t")
            stroke.add(TimedPoint(x, y, t))
        }
        result.add(stroke)
    }
    return result
}

@Composable
private fun DrawingCanvas(
    modifier: Modifier = Modifier,
    strokes: MutableList<List<TimedPoint>>,
    strokeWidth: Float = 6f,
) {
    val currentStroke = remember { mutableStateListOf<TimedPoint>() }

    Canvas(
        modifier =
            modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentStroke.clear()
                        currentStroke.add(TimedPoint(offset.x, offset.y, System.currentTimeMillis()))
                    },
                    onDrag = { change, _ ->
                        val o = change.position
                        currentStroke.add(TimedPoint(o.x, o.y, System.currentTimeMillis()))
                    },
                    onDragEnd = {
                        if (currentStroke.isNotEmpty()) {
                            strokes.add(currentStroke.toList())
                            currentStroke.clear()
                        }
                    },
                    onDragCancel = {
                        currentStroke.clear()
                    },
                )
            },
    ) {
        // Draw existing strokes
        for (stroke in strokes) {
            if (stroke.isEmpty()) continue
            val path = Path()
            path.moveTo(stroke.first().x, stroke.first().y)
            for (p in stroke.drop(1)) {
                path.lineTo(p.x, p.y)
            }
            drawPath(
                path = path,
                color = Color.Black,
                alpha = 1f,
                style =
                    androidx.compose.ui.graphics.drawscope
                        .Stroke(width = strokeWidth),
            )
        }
        // Draw current stroke as it is being drawn
        if (currentStroke.isNotEmpty()) {
            val path =
                Path().apply {
                    moveTo(currentStroke.first().x, currentStroke.first().y)
                    for (p in currentStroke.drop(1)) {
                        lineTo(p.x, p.y)
                    }
                }
            drawPath(
                path = path,
                color = Color.Black,
                alpha = 1f,
                style =
                    androidx.compose.ui.graphics.drawscope
                        .Stroke(width = strokeWidth),
            )
        }
    }
}

private suspend fun ensureDigitalInkModelAvailable(
    languageTag: String =
        java.util.Locale
            .getDefault()
            .toLanguageTag(),
) {
    // Resolve a supported model language with graceful fallbacks
    val preferredTags =
        listOf(
            languageTag,
            languageTag.substringBefore('-'),
            "en-US",
            "en",
        ).distinct().filter { it.isNotBlank() }

    var resolvedModel: DigitalInkRecognitionModel? = null
    var lastError: Throwable? = null
    for (tag in preferredTags) {
        try {
            val id = DigitalInkRecognitionModelIdentifier.fromLanguageTag(tag)
            if (id != null) {
                resolvedModel = DigitalInkRecognitionModel.builder(id).build()
                break
            }
        } catch (t: Throwable) {
            lastError = t
        }
    }

    val model =
        resolvedModel ?: throw IllegalArgumentException(
            "Unsupported language tag(s); tried: ${preferredTags.joinToString()}" + (lastError?.let { ". Cause: ${it.message}" } ?: ""),
        )

    val manager = RemoteModelManager.getInstance()
    val isDownloaded = manager.isModelDownloaded(model).await()
    if (!isDownloaded) {
        // Attempt to download the model if not present
        val conditions =
            DownloadConditions
                .Builder()
                // Do not require Wi‑Fi; small models can be fetched over cellular
                .build()
        manager.download(model, conditions).await()
    }
}

private suspend fun recognizeInkToText(
    strokes: List<List<TimedPoint>>,
    languageTag: String =
        java.util.Locale
            .getDefault()
            .toLanguageTag(),
): String {
    if (strokes.isEmpty()) return ""

    // Build Ink from strokes
    val inkBuilder = Ink.builder()
    for (stroke in strokes) {
        if (stroke.isEmpty()) continue
        val sb = Ink.Stroke.builder()
        for (p in stroke) {
            sb.addPoint(Ink.Point.create(p.x, p.y, p.t))
        }
        inkBuilder.addStroke(sb.build())
    }
    val ink = inkBuilder.build()

    // Resolve a supported model language with graceful fallbacks
    val preferredTags =
        listOf(
            languageTag,
            // Normalize to base language if regional tag isn't supported
            languageTag.substringBefore('-'),
            // Sensible defaults
            "en-US",
            "en",
        ).distinct().filter { it.isNotBlank() }

    var resolvedModel: DigitalInkRecognitionModel? = null
    var lastError: Throwable? = null
    for (tag in preferredTags) {
        try {
            val id = DigitalInkRecognitionModelIdentifier.fromLanguageTag(tag)
            if (id != null) {
                resolvedModel = DigitalInkRecognitionModel.builder(id).build()
                break
            }
        } catch (t: Throwable) {
            lastError = t
        }
    }

    val model = resolvedModel ?: throw IllegalArgumentException("Unsupported language tag(s); tried: ${preferredTags.joinToString()}" + (lastError?.let { ". Cause: ${it.message}" } ?: ""))

    // Ensure model is available on device (download if missing)
    ensureDigitalInkModelAvailable(languageTag)

    // Recognize ink to text
    val recognizer =
        DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build(),
        )
    val result = recognizer.recognize(ink).await()
    return result.candidates
        .firstOrNull()
        ?.text
        .orEmpty()
}
