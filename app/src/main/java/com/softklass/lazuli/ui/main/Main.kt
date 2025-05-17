package com.softklass.lazuli.ui.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softklass.lazuli.data.models.Parent

@Composable
fun Main(
    viewModel: MainViewModel,
    onDetailItemClick: (String) -> Unit
) {
    MainContent(onDetailItemClick, listOf())
}

@Composable
fun MainContent(
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