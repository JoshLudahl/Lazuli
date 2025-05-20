package com.softklass.lazuli.ui.detail

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.Footer
import com.softklass.lazuli.ui.list.HeaderUi
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.particles.useDebounce

@Composable
fun ListDetailScreen(
    listId: Int,
    viewModel: ListDetailViewModel,
    onBack: () -> Unit
) {
    var listItem: String by rememberSaveable { mutableStateOf("") }
    val listItems by viewModel.listItems.collectAsStateWithLifecycle()
    val parent by viewModel.parent.collectAsStateWithLifecycle(null)
    var isEnabled by remember { mutableStateOf(true) }.useDebounce {
        Log.i(
            "Debounce",
            "isEnabled: $it"
        )
    }

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = {
                    isEnabled = false
                    onBack()
                },
                includeBackArrow = true,
                title = {
                    Text(parent?.content ?: "List Detail")
                },
                modifier = Modifier,
                actions = {},
                isEnabled = isEnabled
            )
        }
    ) { innerPadding ->

        ListDetailContent(
            modifier = Modifier.padding(innerPadding),
            listItem = listItem,
            list = listItems,
            onListItemChange = { listItem = it },
            onAddItemClick = {
                viewModel.addListItem(it, listId)
                listItem = ""
            },
            onDeleteItemClick = {
                viewModel.removeItem(it as Item)
            }
        )
    }
}

@Composable
fun ListDetailContent(
    modifier: Modifier = Modifier,
    listItem: String,
    list: List<Item?>,
    onListItemChange: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    onDeleteItemClick: (ListItem) -> Unit
) {

    Column(modifier = modifier) {

        HeaderUi(
            listName = listItem,
            onListNameChange = onListItemChange,
            onAddItemClick = onAddItemClick,
            context = LocalContext.current,
            label = "Add list item"
        )

        if (list.isEmpty()) {
            EmptyList(message = "No items created.")
        } else {
            DisplayList(
                list = list,
                onItemClick = {},
                onDeleteIconClick = onDeleteItemClick
            )
        }

        Footer()
    }
}
