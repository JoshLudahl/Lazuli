package com.softklass.lazuli.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.data.models.Parent
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.HeaderUi
import com.softklass.lazuli.ui.list.SectionTitle
import com.softklass.lazuli.ui.composables.ConfirmationDialog
import com.softklass.lazuli.ui.composables.ReusableTopAppBar
import com.softklass.lazuli.ui.theme.TopAppBarIcon
import com.softklass.lazuli.utils.shareAppIntent

@Composable
fun Main(
    viewModel: MainViewModel,
    onDetailItemClick: (Int) -> Unit,
    onEditItemClick: (ListItem) -> Unit,
    onSettingsClick: () -> Unit,
) {
    var listName: String by rememberSaveable { mutableStateOf("") }
    val items by viewModel.parentItems.collectAsStateWithLifecycle()
    val itemCounts by viewModel.itemCounts.collectAsStateWithLifecycle()
    val openDialog = remember { mutableStateOf(false) }
    val openDeleteListDialog = remember { mutableStateOf(false) }
    val parent = remember { mutableStateOf<Parent?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = {},
                includeBackArrow = false,
                title = {
                    Text("Lazuli")
                },
                modifier = Modifier.testTag("title"),
                actions = {
                    TopAppBarIcon(icon = Icons.Rounded.Share) { shareAppIntent(context) }
                },
                isEnabled = false,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(56.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets =
                    WindowInsets(
                        left = 8.dp,
                        top = 0.dp,
                        right = 0.dp,
                        bottom = 16.dp,
                    ),
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                    )
                }

                IconButton(
                    onClick = {
                        if (items.isNotEmpty()) {
                            openDialog.value = true
                        }
                    },
                    enabled = items.isNotEmpty(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Delete everything.",
                    )
                }
            }
        },
        modifier =
            Modifier
                .padding(start = 8.dp, end = 8.dp)
                .windowInsetsPadding(WindowInsets.systemBars),
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
            },
            onEditItemClick = onEditItemClick,
            itemCounts = itemCounts,
        )

        ConfirmationDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            onConfirmation = {
                viewModel.clearList()
                openDialog.value = false
            },
            dialogTitle = "Clear Lists",
            dialogText = "Are you sure you want to clear this list? This will delete everything from the database.",
            showConfirmation = openDialog.value,
        )

        ConfirmationDialog(
            onDismissRequest = {
                openDeleteListDialog.value = false
            },
            onConfirmation = {
                viewModel.removeItem(parent.value as Parent)
                openDeleteListDialog.value = false
            },
            dialogTitle = "Clear List?",
            dialogText = "Are you sure you want to clear this list? This action will remove the list and all items it contains.",
            showConfirmation = openDeleteListDialog.value,
        )
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
    onDeleteIconClick: (ListItem) -> Unit,
    onEditItemClick: (ListItem) -> Unit,
    itemCounts: Map<Int, Int>,
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
                onEditItemClick = onEditItemClick,
                isListItemDetail = true,
                itemCounts = itemCounts,
            )

            Spacer(modifier = Modifier.weight(.5f))
        }
    }
}
