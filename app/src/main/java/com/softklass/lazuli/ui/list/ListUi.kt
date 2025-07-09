package com.softklass.lazuli.ui.list

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
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
    onItemClick: (Int) -> Unit,
    onDeleteIconClick: (ListItem) -> Unit,
    onEditItemClick: (ListItem) -> Unit,
    isListItemDetail: Boolean = false
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
                        .animateItem()
                        .clickable {
                            onItemClick(item.id)
                        },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 12.dp,
                                    bottom = 12.dp,
                                    end = 8.dp
                                )

                                .align(Alignment.CenterVertically)
                                .weight(.9f)
                                .testTag(item.content),
                            text = it,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (isListItemDetail) {
                            IconButton(
                                onClick = {
                                    onEditItemClick(item)
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.edit_note_24px),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    contentDescription = "Remove list item.",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("edit_icon")
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onDeleteIconClick(item)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag("delete_icon")
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.delete_forever_24px),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Remove list item.",
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("delete_icon")
                            )
                        }

                        if (isListItemDetail) {
                            IconButton(
                                onClick = {
                                    onItemClick(item.id)
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    contentDescription = "Remove list item.",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
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
    DisplayList(
        list,
        onItemClick = {},
        onDeleteIconClick = {},
        onEditItemClick = {},
        isListItemDetail = false
    )
}

@Preview
@Composable
fun EmptyListPreview() {
    EmptyList("No lists created.")
}

fun shareList(title: String = "Lazuli List", list: List<ListItem?>, context: Context) {

    val shareText = buildString {
        appendLine(" List: $title")
        appendLine("----------")
        list.map { it?.content ?: "" }
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .forEach { item ->
                appendLine(item)
            }
        appendLine("----------")
        appendLine("Shared from Lazuli")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val shareIntent = Intent.createChooser(sendIntent, "Sharing List")
    context.startActivity(shareIntent)
}
