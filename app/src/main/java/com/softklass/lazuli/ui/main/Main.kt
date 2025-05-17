package com.softklass.lazuli.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.softklass.lazuli.data.models.Parent

@Composable
fun Main(
    viewModel: MainViewModel,
    onDetailItemClick: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = { FabButton(onClick = { /*TODO*/ }) },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.End
    ) { innerPadding ->
        MainContent(
            modifier = Modifier.padding(innerPadding),
            onDetailItemClick,
            listOf()
        )
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onDetailItemClick: (String) -> Unit,
    list: List<Parent>
) {
    if (list.isEmpty()) {
        EmptyList()
    } else {
        DisplayList(list)
    }
}

@Composable
fun DisplayList(
    list: List<Parent>
) {
    LazyColumn {
        items(list) { item ->
            Text(text = item.description)
        }
    }
}

@Preview
@Composable
fun DisplayListPreview() {
    val list = listOf(
        Parent("1", "Parent 1"),
        Parent("2", "Parent 2"),
        Parent("3", "Parent 3")
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

@Composable
fun FabButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Add, "Add list button.") },
        text = { Text(text = "Add List") },
    )
}