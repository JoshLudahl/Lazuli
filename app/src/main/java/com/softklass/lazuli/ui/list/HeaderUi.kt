package com.softklass.lazuli.ui.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.ui.theme.primaryLight

@Composable
fun HeaderUi(
    listName: String,
    onListNameChange: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    context: Context,
    label: String = "List Name"
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = listName,
            onValueChange = onListNameChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth(.80f)
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryLight,
                unfocusedLabelColor = Color.Gray
            ),
            singleLine = true,
            maxLines = 1
        )

        Button(
            onClick = {
                if (listName.trim().isNotEmpty()) {
                    onAddItemClick(listName.trim())
                } else {
                    Toast.makeText(
                        context,
                        "Please enter a list name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(.80f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add list button.",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Add"
            )
        }
    }
}
