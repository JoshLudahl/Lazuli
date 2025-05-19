package com.softklass.lazuli.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.Footer
import com.softklass.lazuli.ui.list.HeaderUi

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
            viewModel.removeItem(it as Parent)
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
    onDeleteIconClick: (ListItem) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val context = LocalContext.current

        HeaderUi(
            listName = listName,
            onListNameChange = onListNameChange,
            onAddItemClick = onAddItemClick,
            context = context
        )

        if (list.isEmpty()) {
            EmptyList(message = "No lists created.")
        } else {
            DisplayList(
                list,
                onItemClick = onDetailItemClick,
                onDeleteIconClick = onDeleteIconClick
            )

            Spacer(modifier = Modifier.weight(.5f))

            Footer()
        }
    }
}
