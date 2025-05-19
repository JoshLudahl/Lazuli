package com.softklass.lazuli.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.models.Parent

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
        }
    )
}

@Composable
fun MainContent(
    onDetailItemClick: (Int) -> Unit,
    onAddItemClick: (String) -> Unit,
    list: List<Parent?>,
    listName: String,
    onListNameChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Button(
            onClick = { onDetailItemClick(1) },
            modifier = Modifier
                .fillMaxWidth(.80f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        ) {
            Text(text = "Detail")
        }
        OutlinedTextField(
            value = listName,
            onValueChange = onListNameChange,
            label = { Text("List Name") },
            modifier = Modifier
                .fillMaxWidth(.80f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )

        Button(
            onClick = { onAddItemClick(listName) },
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
            DisplayList(list)
        }
    }
}

@Composable
fun DisplayList(
    list: List<Parent?>
) {
    LazyColumn {
        items(list) { item ->
            item?.description?.let { Text(text = it) }
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
