package com.camscanneralter.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.ColorMatrix

enum class ScanFilter {
    Original,
    Magic,
    Ink
}

fun filterMatrix(filter: ScanFilter): ColorMatrix {
    return when (filter) {
        ScanFilter.Original -> ColorMatrix()
        ScanFilter.Magic -> ColorMatrix().apply {
            setToSaturation(1.4f)
            postConcat(ColorMatrix().apply { setScale(1.1f, 1.05f, 1.0f, 1.0f) })
        }
        ScanFilter.Ink -> ColorMatrix().apply {
            setToSaturation(0f)
            postConcat(ColorMatrix().apply { setScale(1.25f, 1.25f, 1.25f, 1.0f) })
        }
    }
}

fun applyFilter(bitmap: Bitmap, filter: ScanFilter): Bitmap {
    if (filter == ScanFilter.Original) return bitmap

    val filteredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(filteredBitmap)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(filterMatrix(filter).values)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return filteredBitmap
}
