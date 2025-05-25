package com.softklass.lazuli.ui.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.data.models.Parent

@Composable
fun DisplayList(
    list: List<ListItem?>,
    onItemClick: (Int) -> Unit = {},
    onDeleteIconClick: (ListItem) -> Unit = {},
    color: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(list) { item ->

            item?.content?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.90f)
                        .padding(8.dp)
                        .clickable {
                            onItemClick(item.id)
                        },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    )
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 8.dp)
                                .weight(.9f),
                            text = it,
                        )
                        Icon(
                            Icons.Filled.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Remove list item.",
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterVertically)
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

@Composable
fun EmptyList(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, modifier = Modifier.align(Alignment.CenterHorizontally))
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

@Preview
@Composable
fun EmptyListPreview() {
    EmptyList("No lists created.")
}
