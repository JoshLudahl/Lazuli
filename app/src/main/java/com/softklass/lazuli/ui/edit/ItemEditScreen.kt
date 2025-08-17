package com.softklass.lazuli.ui.edit

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.particles.useDebounce
import com.softklass.lazuli.ui.theme.primaryLight

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
                actions = { },
                isEnabled = isEnabled,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(56.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets =
                    WindowInsets(
                        left = 8.dp,
                        top = 0.dp,
                        right = 0.dp,
                        bottom = 0.dp,
                    ),
                actions = {
                },
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
            onSaveClick = {
                if (title.trim().isNotEmpty()) {
                    viewModel.saveItem(title, if (viewModel.isParentFlag) null else notes, if (viewModel.isParentFlag) false else notesIsMarkdown)
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
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                            onSaveClick()
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
            // Notes section and markdown toggle
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            )

            // Toggle between Text and Markdown (mode selection similar to Settings)
            @OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
            androidx.compose.foundation.layout.Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                androidx.compose.material3.ToggleButton(
                    checked = !notesIsMarkdown,
                    onCheckedChange = { if (it) onNotesModeChange(false) },
                    modifier = Modifier.weight(1.2f),
                    shapes =
                        androidx.compose.material3.ButtonGroupDefaults
                            .connectedLeadingButtonShapes(),
                ) {
                    Text("Text", maxLines = 1)
                }
                androidx.compose.material3.ToggleButton(
                    checked = notesIsMarkdown,
                    onCheckedChange = { if (it) onNotesModeChange(true) },
                    modifier = Modifier.weight(1.2f),
                    shapes =
                        androidx.compose.material3.ButtonGroupDefaults
                            .connectedTrailingButtonShapes(),
                ) {
                    Text("Markdown", maxLines = 1)
                }
            }

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

        Button(
            onClick = onSaveClick,
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            Text("Save")
        }
    }
}
