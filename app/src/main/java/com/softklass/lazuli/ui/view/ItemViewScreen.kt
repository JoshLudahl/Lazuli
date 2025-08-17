package com.softklass.lazuli.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softklass.lazuli.ui.particles.MarkdownText
import com.softklass.lazuli.ui.particles.ReusableTopAppBar

@Composable
fun ItemViewScreen(
    itemId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: ItemViewViewModel = hiltViewModel(),
) {
    val item by viewModel.item.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                onNavigateBack = onBack,
                includeBackArrow = true,
                title = {
                    Text(text = item?.content ?: "Item")
                },
                actions = {
                    IconButton(onClick = { item?.let { onEdit(it.id) } }) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit item")
                    }
                },
                isEnabled = true,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets =
                    WindowInsets(
                        left = 8.dp,
                        top = 0.dp,
                        right = 0.dp,
                        bottom = 0.dp,
                    ),
                actions = {},
            )
        },
        modifier = Modifier.fillMaxSize().padding(8.dp),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            )
            val notes = item?.notes.orEmpty()
            if (notes.isBlank()) {
                Text(
                    text = "No notes.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else {
                if (item?.notesIsMarkdown == true) {
                    MarkdownText(text = notes)
                } else {
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
