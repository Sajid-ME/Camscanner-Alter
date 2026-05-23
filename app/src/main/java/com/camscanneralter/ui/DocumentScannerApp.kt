package com.camscanneralter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.camscanneralter.ui.theme.CamscannerAlterTheme

private enum class ScreenState {
    Home,
    Camera,
    Result
}

@Composable
fun DocumentScannerApp() {
    val screenState = remember { mutableStateOf(ScreenState.Home) }
    val capturedImagePath = remember { mutableStateOf<String?>(null) }

    CamscannerAlterTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Camscanner Alter") },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )

                when (screenState.value) {
                    ScreenState.Home -> HomeScreen(onStartScan = { screenState.value = ScreenState.Camera })
                    ScreenState.Camera -> CameraScreen(
                        onCaptured = { path ->
                            capturedImagePath.value = path
                            screenState.value = ScreenState.Result
                        },
                        onBack = { screenState.value = ScreenState.Home }
                    )
                    ScreenState.Result -> ScanResultScreen(
                        capturedImagePath = capturedImagePath.value,
                        onRetake = { screenState.value = ScreenState.Camera },
                        onDone = { screenState.value = ScreenState.Home }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onStartScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Privacy-first scanning with smart filters",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface
        )

        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = onStartScan,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Text(modifier = Modifier.padding(start = 8.dp), text = "Start Scan")
        }
    }
}
