package com.softklass.lazuli.ui.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(title: String) {
    BasicText(
        text = title,
        style =
            MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
        autoSize =
            TextAutoSize.StepBased(
                minFontSize = 12.sp,
                maxFontSize = 16.sp,
            ),
    )
}
