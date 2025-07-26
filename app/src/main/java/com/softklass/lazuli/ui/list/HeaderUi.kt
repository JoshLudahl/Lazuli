package com.softklass.lazuli.ui.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.R
import com.softklass.lazuli.ui.theme.primaryLight

@Composable
fun HeaderUi(
    listName: String,
    onListNameChange: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    context: Context,
    label: String = "List Name",
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = listName,
            onValueChange = onListNameChange,
            label = { Text(label) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .testTag("list"),
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        if (listName.trim().isNotEmpty()) {
                            onAddItemClick(listName.trim())
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
            trailingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.post_add_24px),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "Add list button.",
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .size(36.dp)
                            .clickable {
                                if (listName.trim().isNotEmpty()) {
                                    onAddItemClick(listName.trim())
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please enter valid text.",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                }
                            }.testTag("add_list_icon"),
                )
            },
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        )
    }
}

@Composable
fun DeleteIcon(
    onClickIcon: () -> Unit,
) {
    FilledIconButton(
        onClick = onClickIcon,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.delete_sweep_24px),
            contentDescription = "Back",
            modifier = Modifier.testTag("clear"),
        )
    }
}

@Preview
@Composable
fun HeaderUiPreview() {
    HeaderUi(
        listName = "",
        onListNameChange = { },
        onAddItemClick = { },
        context = LocalContext.current,
    )
}
