package com.softklass.lazuli.ui.detail

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.R
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.ui.camera.CameraScreen
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.HeaderUi
import com.softklass.lazuli.ui.list.SectionTitle
import com.softklass.lazuli.ui.list.shareList
import com.softklass.lazuli.ui.particles.ConfirmationDialog
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.particles.useDebounce
import kotlinx.coroutines.launch

@Composable
fun ListDetailScreen(
    listId: Int,
    viewModel: ListDetailViewModel,
    onBack: () -> Unit,
    onEditItemClick: (ListItem) -> Unit,
) {
    val sorted by viewModel.sorted.collectAsStateWithLifecycle()
    var listItem: String by rememberSaveable { mutableStateOf("") }
    val listItems by viewModel.listItems.collectAsStateWithLifecycle()
    val processingImage by viewModel.processingImage.collectAsStateWithLifecycle()

    val parent by viewModel.parent.collectAsStateWithLifecycle(null)
    var isEnabled by remember { mutableStateOf(true) }.useDebounce {
        Log.i(
            "Debounce",
            "isEnabled: $it",
        )
    }

    val openDialog = remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                actions = { },
                isEnabled = isEnabled,
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
                actions = {
                    IconButton(
                        onClick = {
                            openDialog.value = true
                        },
                        enabled = listItems.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.delete_sweep_24px),
                            contentDescription = "Delete",
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.toggleSort()
                        },
                        enabled = listItems.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.sort_24px),
                            contentDescription = "Sort",
                        )
                    }

                    IconButton(
                        onClick = {
                            shareList(
                                title = parent?.content ?: "",
                                list = listItems,
                                context = context,
                            )
                        },
                        enabled = listItems.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                        )
                    }
                },
            )
        },
        modifier = Modifier.padding(8.dp),
    ) { innerPadding ->

        // Camera screen dialog
        if (showCamera) {
            Dialog(
                onDismissRequest = { showCamera = false },
            ) {
                CameraScreen(
                    onImageCaptured = { bitmap ->
                        coroutineScope.launch {
                            viewModel.processImageForOcr(bitmap)
                            showCamera = false
                        }
                    },
                    onClose = { showCamera = false },
                    isProcessing = processingImage,
                )
            }
        }

        ListDetailContent(
            modifier = Modifier.padding(innerPadding),
            listItem = listItem,
            list =
                if (sorted) {
                    listItems.sortedWith { first, second ->
                        second?.content?.let {
                            first?.content?.compareTo(
                                it,
                                ignoreCase = true,
                            )
                        } ?: 0
                    }
                } else {
                    listItems
                },
            onListItemChange = { listItem = it },
            onAddItemClick = {
                viewModel.addListItem(it, listId)
                listItem = ""
            },
            onDeleteItemClick = {
                viewModel.removeItem(it as Item)
            },
            onEditItemClick = onEditItemClick,
            onCameraClick = { showCamera = true },
        )

        ConfirmationDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            onConfirmation = {
                viewModel.clearList(parentId = listId)
                openDialog.value = false
            },
            dialogTitle = "Clear List",
            dialogText = "Are you sure you want to clear this list?",
            showConfirmation = openDialog.value,
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
    onDeleteItemClick: (ListItem) -> Unit,
    onEditItemClick: (ListItem) -> Unit,
    onCameraClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Add New Item")

        HeaderUi(
            listName = listItem,
            onListNameChange = onListItemChange,
            onAddItemClick = onAddItemClick,
            onCameraClick = onCameraClick,
            context = LocalContext.current,
            label = "Add list item",
        )

        if (list.isEmpty()) {
            EmptyList(message = "No items created.")
        } else {
            SectionTitle("Items")
            DisplayList(
                list = list,
                onItemClick = {},
                onDeleteIconClick = onDeleteItemClick,
                onEditItemClick = onEditItemClick,
                isListItemDetail = false,
            )
        }
    }
}
