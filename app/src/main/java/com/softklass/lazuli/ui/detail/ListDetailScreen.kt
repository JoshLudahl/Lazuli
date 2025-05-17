package com.softklass.lazuli.ui.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ListDetailScreen(
    listId: String,
    viewModel: ListDetailViewModel,
    onBack: () -> Unit
) {
    Text(text = "List Detail Screen for $listId")
}
