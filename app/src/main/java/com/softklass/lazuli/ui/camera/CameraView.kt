package com.softklass.lazuli.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume

@Composable
fun CameraView(
    onImageCaptured: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    isProcessing: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var previewUseCase by remember { mutableStateOf<Preview?>(null) }
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture
                .Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setResolutionSelector(
                    ResolutionSelector
                        .Builder()
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                Size(1920, 1080),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                            ),
                        ).build(),
                ).build(),
        )
    }

    // Camera selector
    val cameraSelector =
        remember {
            CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        }

    // Set up the camera
    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        previewUseCase = Preview.Builder().build()

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCaptureUseCase,
            )
        } catch (e: Exception) {
            Log.e("CameraView", "Use case binding failed", e)
        }
    }

    // Create a Surface with a frame that has rounded corners and uses the secondary color
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            // Inner Surface for the camera preview with a white background
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        previewView
                    },
                    update = { previewView ->
                        previewUseCase?.surfaceProvider = previewView.surfaceProvider
                    },
                )
            }

            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = imageCaptureUseCase.takePicture(context.executor)
                                onImageCaptured(bitmap)
                            } catch (e: Exception) {
                                Log.e("CameraView", "Failed to take picture", e)
                                onError(
                                    ImageCaptureException(
                                        ImageCapture.ERROR_UNKNOWN,
                                        "Failed to capture image",
                                        e,
                                    ),
                                )
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondary,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = "Take photo",
                            Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSecondary,
                        )
                    }
                }
            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener(
                {
                    continuation.resume(future.get())
                },
                executor,
            )
        }
    }

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

suspend fun ImageCapture.takePicture(executor: Executor): Bitmap =
    suspendCancellableCoroutine { continuation ->
        takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.decodeToBitmapWithDownsampling()
                    if (bitmap == null) {
                        continuation.resumeWith(Result.failure(Exception("Failed to decode bitmap")))
                        image.close()
                        return
                    }
                    val rotatedBitmap =
                        bitmap.rotateBitmap(image.imageInfo.rotationDegrees.toFloat())
                    image.close()
                    continuation.resume(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraView", "Image capture failed", exception)
                    continuation.resumeWith(Result.failure(exception))
                }
            },
        )
    }

fun Bitmap.rotateBitmap(rotationDegrees: Float): Bitmap {
    val matrix =
        Matrix().apply {
            postRotate(rotationDegrees)
        }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun ImageProxy.decodeToBitmapWithDownsampling(): Bitmap? {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val options =
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

    // Target ~1080p dimensions for OCR to reduce memory pressure
    options.inSampleSize = calculateInSampleSize(options, 1920, 1080)
    options.inJustDecodeBounds = false

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
}

fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
