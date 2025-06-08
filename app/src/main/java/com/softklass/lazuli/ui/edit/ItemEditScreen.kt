package com.softklass.lazuli.ui.edit

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.particles.useDebounce

@Composable
fun ItemEditScreen(
    viewModel: ItemEditViewModel,
    onBack: () -> Unit,
    itemId: Int
) {

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
                    Text("Edit")
                },
                actions = { },
                isEnabled = isEnabled
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(56.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets(
                    left = 8.dp,
                    top = 0.dp,
                    right = 0.dp,
                    bottom = 0.dp
                ),
                actions = {
                }
            )

        },
        modifier = Modifier.padding(8.dp)
    ) { innerPadding ->
        ItemDetailScreenContent(
            modifier = Modifier.padding(innerPadding),
            value = "",
            onValueChange = {},
            onSaveClick = {}
        )
    }
}

@Composable
fun ItemDetailScreenContent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        OutlinedTextField(
            value = "",
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(.80f).padding(top = 16.dp)
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(.80f)
        ) {
            Text("Save")
        }
    }
}

@Preview
@Composable
fun ItemEditScreenPreview() {
    ItemDetailScreenContent(
        value = "",
        onValueChange = {},
        onSaveClick = {}
    )
}
