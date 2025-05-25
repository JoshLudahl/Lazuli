package com.softklass.lazuli.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.R
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
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .padding(bottom = 8.dp, top = 8.dp)
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.delete_24px),
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

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.work),
                    contentDescription = "Empty list icon", // Provide meaningful description for accessibility
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(.50f)
                )
                Text(text = message, modifier = Modifier.padding(top = 8.dp))

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

@Preview
@Composable
fun EmptyListPreview() {
    EmptyList("No lists created.")
}
