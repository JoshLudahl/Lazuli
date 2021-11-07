package com.softklass.listuous

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.listuous.models.Item
import com.softklass.listuous.models.ItemList
import com.softklass.listuous.models.ListOfItemList
import com.softklass.listuous.ui.theme.Blue
import com.softklass.listuous.ui.theme.LightOrange
import com.softklass.listuous.ui.theme.Orange
import com.softklass.listuous.ui.theme.Peach

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
                .background(color = Blue)
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
        Row {
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
        Row(Modifier.fillMaxWidth()) {
            Card()
        }

    }
}

@Composable
fun Header() {
    Text("Hello Header!")
}

@Composable
fun ListContainer(listOfLists: ListOfItemList) {
    LazyColumn(Modifier.background(color = Blue)) {
        items(listOfLists.lists.size) { message ->
            ItemListItem(listOfLists.lists.elementAt(message))
        }
    }
}

@Composable
fun ItemListItem(itemsList: ItemList) {
    Row {
        Text(text = itemsList.listName, color = LightOrange)
    }
}

@Composable
fun Footer() {
    val context = LocalContext.current
    Column {
        Row {
            Text("Hello Footer!", color = Orange)
        }
        Row {
            FloatingActionButton(
                onClick = { Toast.makeText(context, "Whoo", Toast.LENGTH_LONG).show() },
                Modifier
                    .height(40.dp)
                    .width(40.dp),
                backgroundColor = Peach
            ) {
            }
        }
    }
}

@Composable
fun Card() {
    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(width = maxWidth, height = maxHeight)
                .background(color = Peach)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(10.dp)
            ) {
                Card(
                    backgroundColor = Blue,
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("AB CDE", fontWeight = FontWeight.W700)
                        Text("+0 12345678")
                        Text("XYZ city.", color = Color.Gray)
                    }
                }
            }
        }
    }
}
