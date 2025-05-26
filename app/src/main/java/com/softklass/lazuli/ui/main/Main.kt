package com.softklass.lazuli.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.ui.list.DeleteIcon
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.HeaderUi
import com.softklass.lazuli.ui.list.SectionTitle
import com.softklass.lazuli.ui.particles.ConfirmationDialog
import com.softklass.lazuli.ui.particles.ReusableTopAppBar

@Composable
fun Main(
    viewModel: MainViewModel,
    onDetailItemClick: (Int) -> Unit
) {
    var listName: String by rememberSaveable { mutableStateOf("") }
    val items by viewModel.parentItems.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }
    val openDeleteListDialog = remember { mutableStateOf(false) }
    val parent = remember { mutableStateOf<Parent?>(null) }

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = {},
                includeBackArrow = false,
                title = {
                    Text("Lazuli")
                },
                modifier = Modifier,
                actions = {
                    if (items.isNotEmpty()) {
                        DeleteIcon { openDialog.value = true }
                    }
                },
                isEnabled = false
            )
        }
    ) { innerPadding ->

        MainContent(
            modifier = Modifier.padding(innerPadding),
            onDetailItemClick = onDetailItemClick,
            list = items,
            listName = listName,
            onListNameChange = { listName = it },
            onAddItemClick = {
                viewModel.addList(it)
                listName = ""
            },
            onDeleteIconClick = {
                parent.value = it as Parent
                openDeleteListDialog.value = true
            }
        )

        if (openDialog.value) {
            ConfirmationDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                onConfirmation = {
                    viewModel.clearList()
                    openDialog.value = false
                },
                dialogTitle = "Clear Lists",
                dialogText = "Are you sure you want to clear this list? This will clear all lists."
            )
        }

        if (openDeleteListDialog.value) {
            ConfirmationDialog(
                onDismissRequest = {
                    openDeleteListDialog.value = false
                },
                onConfirmation = {
                    viewModel.removeItem(parent.value as Parent)
                    openDeleteListDialog.value = false
                },
                dialogTitle = "Clear List?",
                dialogText = "Are you sure you want to clear this list? This action will remove the list and all items it contains."
            )
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onDetailItemClick: (Int) -> Unit,
    onAddItemClick: (String) -> Unit,
    list: List<Parent?>,
    listName: String,
    onListNameChange: (String) -> Unit,
    onDeleteIconClick: (ListItem) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current

        SectionTitle(title = "Create New List")

        HeaderUi(
            listName = listName,
            onListNameChange = onListNameChange,
            onAddItemClick = onAddItemClick,
            context = context,
        )

        if (list.isEmpty()) {
            EmptyList(message = "No lists created.")
        } else {
            SectionTitle("Lists")
            DisplayList(
                list,
                onItemClick = onDetailItemClick,
                onDeleteIconClick = onDeleteIconClick,
                shouldShowNextIcon = true
            )

            Spacer(modifier = Modifier.weight(.5f))
        }
    }
}
