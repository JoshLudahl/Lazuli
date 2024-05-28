package com.softklass.lazuli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.data.models.Category
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.ItemList
import com.softklass.lazuli.data.models.Label
import com.softklass.lazuli.data.models.ListOfItemList
import com.softklass.lazuli.ui.theme.primaryLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenOutline()
        }
    }
}

@Preview
@Composable
fun ScreenOutline() {
    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(width = maxWidth, height = maxHeight)
        ) {
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.align(CenterHorizontally)) {
            ListTitle()
        }
        Row {
            val list = ListOfItemList(
                lists = mutableListOf()
            )
            list.lists.add(
                ItemList(
                    "Grocery",
                    mutableListOf(
                        Item(
                            content = " this is an item",
                        category = listOf(Category("coins")),
                            label = listOf(Label("something"))
                        )
                    )
                )

            )
            list.lists.add(
                ItemList(
                    "To-Do",
                    mutableListOf(
                        Item(
                            content = " this is an item",
                            category = listOf(Category("coins")),
                            label = listOf(Label("something"))
                        )
                    )

                )

            )
            list.lists.add(
                ItemList(
                    "Errands",
                    mutableListOf(
                        Item(
                            content = " this is an item",
                            category = listOf(Category("coins")),
                            label = listOf(Label("something"))
                        )
                    )
                )
            )
            ListContainer(list)
        }
        Row {
            SimpleFilledTextFieldSample()
        }
    }
}

@Composable
fun ListTitle() {

    Text("List Title")
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
        CustomCart(itemsList = itemsList)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomCart(itemsList: ItemList) {
    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .size(width = maxWidth, height = maxHeight)

        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),

                ) {
                    Column(modifier = Modifier.padding(10.dp).background(primaryLight)) {
                        Text(text = itemsList.listName)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleFilledTextFieldSample() {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("New Item") }
    )
}
