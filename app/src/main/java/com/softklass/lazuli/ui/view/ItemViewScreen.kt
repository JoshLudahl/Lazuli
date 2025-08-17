package com.softklass.lazuli.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import com.halilibo.richtext.markdown.BasicMarkdown // Or other specific components
import com.halilibo.richtext.ui.BasicRichText
import com.softklass.lazuli.ui.particles.ReusableTopAppBar
import com.softklass.lazuli.ui.theme.TopAppBarIcon

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
                    TopAppBarIcon(icon = Icons.Rounded.EditNote) { item?.let { onEdit(it.id) } }
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
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),
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
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            )
            // Notes Card
            val notes = item?.notes.orEmpty()

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                ) {
                    // Notes
                    if (notes.isBlank()) {
                        Text(
                            text = "No notes.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    } else {
                        if (item?.notesIsMarkdown == true) {
                            MarkdownBlock(text = notes)
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
    }
}

@Composable
fun MarkdownBlock(
    text: String,
) {
    val parser = remember { CommonmarkAstNodeParser() }
    val textAsNote = remember(parser, text) { parser.parse(text) }
    BasicRichText(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        BasicMarkdown(astNode = textAsNote)
    }
}
