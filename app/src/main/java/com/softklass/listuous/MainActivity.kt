package com.softklass.listuous

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.listuous.models.Item
import com.softklass.listuous.models.ItemList
import com.softklass.listuous.models.ListOfItemList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }
    }
}

@Preview
@Composable
fun MainLayout() {
    Column {
        Row() {
            Header()
        }
        Row {
            val list = ListOfItemList(
                lists = mutableListOf()
            )
            list.lists.add(
                ItemList(
                    "Grocery",
                    mutableListOf(
                        Item(" this is an item")
                    )
                )

            )
            list.lists.add(
                ItemList(
                    "To-Do",
                    mutableListOf(
                        Item(" this is an item")
                    )
                )

            )
            list.lists.add(
                ItemList(
                    "Errands",
                    mutableListOf(
                        Item(" this is an item")
                    )
                )
            )
            ListContainer(list)
        }
        Row {
            Footer()
        }
    }
}

@Composable
fun Header() {
    Text("Hello Header!", color = Color.Magenta)
}

@Composable
fun ListContainer(listOfLists: ListOfItemList) {
    LazyColumn {
        items(listOfLists.lists.size) { message ->
            ItemListItem(listOfLists.lists.elementAt(message))
        }
    }
}

@Composable
fun ItemListItem(itemsList: ItemList) {
    Row {
        Text(text = itemsList.listName)
    }
}

@Composable
fun Footer() {
    val context = LocalContext.current
    Column {
        Row {
            Text("Hello Footer!", color = Color.Magenta)
        }
        Row {
            FloatingActionButton(
                onClick = { Toast.makeText(context, "Whoo", Toast.LENGTH_LONG).show() },
                Modifier
                    .height(40.dp)
                    .width(40.dp),
                backgroundColor = Color.Magenta
            ) {
            }
        }
    }
}
