package com.camscanneralter.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ScanResultScreen(capturedImagePath: String?, onRetake: () -> Unit, onDone: () -> Unit) {
    val context = LocalContext.current
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
                Button(
                    onClick = {
                        saveBitmapToGallery(context, scanBitmap, selectedFilter.value)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save to Gallery")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Save")
                }
                Button(onClick = onDone, shape = RoundedCornerShape(20.dp), modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Icon(Icons.Default.Check, contentDescription = "Done")
                    Text(modifier = Modifier.padding(start = 8.dp), text = "Done")
                }
            }
        }
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filter: ScanFilter) {
    val filteredBitmap = applyFilter(bitmap, filter)
    val displayName = "CamScannerAlter_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CamScannerAlter")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            if (outputStream != null) {
                filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    } ?: Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
}


@Composable
private fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(100.dp, 44.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            contentColor = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = label)
    }
}