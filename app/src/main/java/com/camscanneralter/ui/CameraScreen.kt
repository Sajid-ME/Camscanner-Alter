package com.camscanneralter.ui

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun CameraScreen(onCaptured: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val hasPermission = rememberSaveable { mutableStateOf(false) }
    val captureError = remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission.value = granted }
    )
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY).build() }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    var cameraIsBound by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(hasPermission.value) {
        onDispose {
            if (cameraIsBound) {
                val provider = ProcessCameraProvider.getInstance(context).get()
                provider.unbindAll()
                cameraIsBound = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                if (hasPermission.value) {
                    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) { preview ->
                        if (!cameraIsBound) {
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener(
                                {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val previewUseCase = Preview.Builder().build().also {
                                        it.setSurfaceProvider(preview.surfaceProvider)
                                    }
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageCapture)
                                        cameraIsBound = true
                                    } catch (exc: Exception) {
                                        captureError.value = "Camera unavailable"
                                    }
                                },
                                ContextCompat.getMainExecutor(context)
                            )
                        }
                    }

                    CropOverlay(modifier = Modifier.fillMaxSize())
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.surface)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Camera permission is required to scan documents.",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Grant camera access")
                        }
                    }
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Back")
                }
            }

            if (captureError.value != null) {
                Text(
                    text = captureError.value ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = {
                    val file = createImageFile(context)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onCaptured(file.absolutePath)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            captureError.value = "Failed to capture image"
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Capture")
                Text(modifier = Modifier.padding(start = 8.dp), text = "Capture")
            }
        }
    }
}

@Composable
private fun CropOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(320.dp)
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.65f)
                .fillMaxHeight(0.65f)
                .border(width = 1.dp, color = Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
        )
    }
}

private fun createImageFile(context: Context): File {
    val directory = context.cacheDir
    return File(directory, "scan_${System.currentTimeMillis()}.jpg")
}
