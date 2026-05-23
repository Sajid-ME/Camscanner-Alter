package com.camscanneralter.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ScanResultScreen(capturedImagePath: String?, onRetake: () -> Unit, onDone: () -> Unit) {
    val selectedFilter = remember { mutableStateOf(ScanFilter.Original) }
    val scanBitmap = remember(capturedImagePath) {
        capturedImagePath?.let { BitmapFactory.decodeFile(it) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        if (scanBitmap == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No scanned image available.",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )

                Button(onClick = onRetake, modifier = Modifier.padding(top = 24.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retake")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Try Again")
                }
            }
            return@Surface
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Image(
                    bitmap = scanBitmap.asImageBitmap(),
                    contentDescription = "Scanned document",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.colorMatrix(filterMatrix(selectedFilter.value))
                )
            }

            Text(
                text = "Filter preview",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterButton(label = "Original", selected = selectedFilter.value == ScanFilter.Original) { selectedFilter.value = ScanFilter.Original }
                FilterButton(label = "Magic", selected = selectedFilter.value == ScanFilter.Magic) { selectedFilter.value = ScanFilter.Magic }
                FilterButton(label = "Ink", selected = selectedFilter.value == ScanFilter.Ink) { selectedFilter.value = ScanFilter.Ink }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onRetake, shape = RoundedCornerShape(20.dp), modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retake")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Retake")
                }
                Button(onClick = onDone, shape = RoundedCornerShape(20.dp), modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Icon(Icons.Default.Check, contentDescription = "Done")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Done")
                }
            }
        }
    }
}

@Composable
private fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(100.dp, 44.dp),
        colors = androidx.compose.material.ButtonDefaults.buttonColors(
            backgroundColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            contentColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = label)
    }
}
