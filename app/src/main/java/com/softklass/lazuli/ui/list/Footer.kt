package com.softklass.lazuli.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Footer() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier =
                Modifier
                    .fillMaxWidth(.80f)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
        ) {
            Text(text = "Clear List")
        }
    }
}
