package com.camscanneralter.ui

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
