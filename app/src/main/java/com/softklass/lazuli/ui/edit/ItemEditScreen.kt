package com.softklass.lazuli.ui.edit

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
    var fieldValue by rememberSaveable(item) { mutableStateOf(item?.content ?: "") }

    var isEnabled by remember { mutableStateOf(true) }.useDebounce {
        Log.i(
            "Debounce",
            "isEnabled: $it",
        )
    }

    val context = LocalContext.current
    Log.i("ItemEditScreen", "itemId: ${item?.content}")
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
            modifier = Modifier.padding(innerPadding),
            value = fieldValue,
            label = fieldValue,
            onValueChange = { fieldValue = it },
            onSaveClick = {
                if (fieldValue.trim().isNotEmpty()) {
                    viewModel.saveItem(fieldValue)
                    onBack()
                } else {
                    Toast
                        .makeText(
                            context,
                            "Please enter a valid list name.",
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
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            label = { Text(label) },
            value = value,
            onValueChange = onValueChange,
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
                        if (value.trim().isNotEmpty()) {
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

        Button(
            onClick = onSaveClick,
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
        ) {
            Text("Save")
        }
    }
}

@PreviewLightDark
@Composable
fun ItemEditScreenPreview() {
    ItemDetailScreenContent(
        value = "",
        onValueChange = {},
        onSaveClick = {},
        label = "List Name",
    )
}
