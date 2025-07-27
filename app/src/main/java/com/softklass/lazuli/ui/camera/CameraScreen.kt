package com.softklass.lazuli.ui.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    onImageCaptured: suspend (Bitmap) -> Unit,
    onClose: () -> Unit,
    isProcessing: Boolean,
) {
    var captureError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPermissionHandler(
            onPermissionGranted = {
                CameraView(
                    onImageCaptured = { bitmap ->
                        coroutineScope.launch {
                            onImageCaptured(bitmap)
                        }
                    },
                    onError = { exception ->
                        captureError = exception.message
                    },
                    isProcessing = isProcessing,
                )
            },
            onPermissionDenied = {
                CameraPermissionDeniedContent()
            },
        )

        // Close button
        FilledIconButton(
            onClick = onClose,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
            shape = CircleShape,
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close camera",
            )
        }

        // Error message if any
        captureError?.let { error ->
            androidx.compose.material3.Text(
                text = "Error: $error",
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
