package com.softklass.lazuli.ui.detail

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.R
import com.softklass.lazuli.data.models.Item
import com.softklass.lazuli.data.models.ListItem
import com.softklass.lazuli.data.models.SortOption
import com.softklass.lazuli.data.models.getSortedList
import com.softklass.lazuli.ui.camera.CameraScreen
import com.softklass.lazuli.ui.composables.ConfirmationDialog
import com.softklass.lazuli.ui.composables.Loading
import com.softklass.lazuli.ui.composables.ReusableTopAppBar
import com.softklass.lazuli.ui.composables.useDebounce
import com.softklass.lazuli.ui.list.DisplayList
import com.softklass.lazuli.ui.list.EmptyList
import com.softklass.lazuli.ui.list.HeaderUi
import com.softklass.lazuli.ui.list.SectionTitle
import com.softklass.lazuli.ui.list.shareList
import kotlinx.coroutines.launch

@Composable
fun ListDetailScreen(
    listId: Int,
    viewModel: ListDetailViewModel,
    onBack: () -> Unit,
    onEditItemClick: (ListItem) -> Unit,
    onViewItemClick: (Int) -> Unit,
) {
    val sorted by viewModel.sorted.collectAsStateWithLifecycle()
    var listItem: String by rememberSaveable { mutableStateOf("") }
    val listItems by viewModel.listItems.collectAsStateWithLifecycle()
    val processingImage by viewModel.processingImage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
    var showMenu by remember { mutableStateOf(false) }

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
                    Box {
                        // We use a Box to anchor the DropdownMenu
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort, // Or your custom sort icon
                                contentDescription = "Sort Options",
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Ascending") },
                                onClick = {
                                    // Handle sort ascending
                                    viewModel.sortByOption(SortOption.ASCENDING)
                                    showMenu = false // Dismiss the menu
                                },
                                colors =
                                    MenuDefaults.itemColors(
                                        textColor = sortedMenuOption(SortOption.ASCENDING, sorted),
                                    ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Sort, // Or your custom sort icon
                                        contentDescription = "Sort by Ascending",
                                    )
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Descending") },
                                onClick = {
                                    // Handle sort descending
                                    viewModel.sortByOption(SortOption.DESCENDING)
                                    showMenu = false // Dismiss the menu
                                },
                                colors =
                                    MenuDefaults.itemColors(
                                        textColor = sortedMenuOption(SortOption.DESCENDING, sorted),
                                    ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Sort, // Or your custom sort icon
                                        contentDescription = "Sort by Descending",
                                        modifier = Modifier.rotate(180f),
                                    )
                                },
                            )

                            // Sort by Due Date
                            DropdownMenuItem(
                                text = { Text("Sort by Due Date") },
                                onClick = {
                                    // Handle sort by date
                                    viewModel.sortByOption(SortOption.DATE)
                                    showMenu = false
                                },
                                colors =
                                    MenuDefaults.itemColors(
                                        textColor = sortedMenuOption(SortOption.DATE, sorted),
                                    ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.DateRange, // Or your custom date icon
                                        contentDescription = "Sort by Due Date icon",
                                    )
                                },
                            )

                            // Sort by Created At
                            DropdownMenuItem(
                                text = { Text("Sort by Created") },
                                onClick = {
                                    // Handle sort by date
                                    viewModel.sortByOption(SortOption.CREATED)
                                    showMenu = false
                                },
                                colors =
                                    MenuDefaults.itemColors(
                                        textColor = sortedMenuOption(SortOption.CREATED, sorted),
                                    ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth, // Or your custom date icon
                                        contentDescription = "Sort by Created Date icon",
                                    )
                                },
                            )
                        }
                    }

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
        modifier =
            Modifier
                .padding(start = 8.dp, end = 8.dp),
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

        if (isLoading) {
            Loading(modifier = Modifier.padding(innerPadding))
        } else {
            ListDetailContent(
                modifier = Modifier.padding(innerPadding),
                listItem = listItem,
                list = getSortedList(sortByOption = sorted, list = listItems),
                onListItemChange = { listItem = it },
                onAddItemClick = {
                    viewModel.addListItem(it, listId)
                    listItem = ""
                },
                onDeleteItemClick = {
                    viewModel.removeItem(it as Item)
                },
                onEditItemClick = onEditItemClick,
                onItemClick = onViewItemClick,
                onCameraClick = { showCamera = true },
            )
        }

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
    onItemClick: (Int) -> Unit,
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

        AnimatedVisibility(list.isEmpty()) {
            EmptyList(message = "No items created.")
        }

        SectionTitle("Items")
        DisplayList(
            list = list,
            onItemClick = onItemClick,
            onDeleteIconClick = onDeleteItemClick,
            onEditItemClick = onEditItemClick,
            isListItemDetail = false,
        )
    }
}

@Composable
private fun sortedMenuOption(
    sortByOption: SortOption,
    sorted: SortOption,
): Color {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onBackground
    return if (sortByOption == sorted) selectedColor else unselectedColor
}
