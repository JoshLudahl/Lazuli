package com.softklass.lazuli.ui.main

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.ui.theme.primaryLight

@Composable
fun Main(
    viewModel: MainViewModel,
    onDetailItemClick: (Int) -> Unit
) {
    var listName: String by rememberSaveable { mutableStateOf("") }
    val items by viewModel.parentItems.collectAsStateWithLifecycle()

    MainContent(
        onDetailItemClick = onDetailItemClick,
        list = items,
        listName = listName,
        onListNameChange = { listName = it },
        onAddItemClick = {
            viewModel.addList(it)
            listName = ""
        },
        onDeleteIconClick = {
            viewModel.removeItem(it)
        }
    )
}

@Composable
fun MainContent(
    onDetailItemClick: (Int) -> Unit,
    onAddItemClick: (String) -> Unit,
    list: List<Parent?>,
    listName: String,
    onListNameChange: (String) -> Unit,
    onDeleteIconClick: (Parent) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val context = LocalContext.current


        OutlinedTextField(
            value = listName,
            onValueChange = onListNameChange,
            label = { Text("List Name") },
            modifier = Modifier
                .fillMaxWidth(.80f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = primaryLight,
                unfocusedLabelColor = Color.Gray
            )
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
                text = "Add List",
            )
        }

        if (list.isEmpty()) {
            EmptyList()
        } else {
            DisplayList(
                list,
                onItemClick = onDetailItemClick,
                onDeleteIconClick = onDeleteIconClick
            )

            Spacer(modifier = Modifier.weight(.5f))

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth(.80f)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Clear List")
            }
        }
    }
}

@Composable
fun DisplayList(
    list: List<Parent?>,
    onItemClick: (Int) -> Unit = {},
    onDeleteIconClick: (Parent) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.85f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { item ->
            item?.description?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.80f)
                        .padding(8.dp)
                        .clickable {
                            onItemClick(item.id)
                        },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                    //border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(.75f),
                            text = it,
                        )
                        Icon(
                            Icons.Filled.Delete,
                            tint = Color.Gray,
                            contentDescription = "Remove list item.",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onDeleteIconClick(item)
                                }

                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DisplayListPreview() {
    val list = listOf(
        Parent(1, "Parent 1"),
        Parent(2, "Parent 2"),
        Parent(3, "Parent 3")
    )
    DisplayList(list)
}

@Composable
fun EmptyList() {
    Text(text = "No items")
}

@Preview
@Composable
fun EmptyListPreview() {
    EmptyList()
}
