package com.camscanneralter.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CropOverlay(modifier: Modifier = Modifier, onPointsChanged: (List<Offset>) -> Unit = {}) {
    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth
        val height = maxHeight
        val defaultPoints = listOf(
            Offset(x = width.value * 0.15f, y = height.value * 0.15f),
            Offset(x = width.value * 0.85f, y = height.value * 0.15f),
            Offset(x = width.value * 0.85f, y = height.value * 0.85f),
            Offset(x = width.value * 0.15f, y = height.value * 0.85f)
        )

        val pointsState = remember { mutableStateOf(defaultPoints) }
        val points = pointsState.value

        LaunchedEffect(width, height) {
            if (points.all { it == Offset.Zero }) {
                pointsState.value = defaultPoints
                onPointsChanged(defaultPoints)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
                .pointerInput(points) {
                    detectDragGestures(
                        onDragStart = {},
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val closestIndex = points.minByOrNull { it.distanceTo(change.position) }?.let { points.indexOf(it) } ?: 0
                            val updated = points.toMutableList()
                            val moved = (updated[closestIndex] + dragAmount).coerceInBounds(width.value, height.value)
                            updated[closestIndex] = moved
                            pointsState.value = updated
                            onPointsChanged(updated)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    reset()
                    moveTo(points[0].x, points[0].y)
                    lineTo(points[1].x, points[1].y)
                    lineTo(points[2].x, points[2].y)
                    lineTo(points[3].x, points[3].y)
                    close()
                }

                drawPath(path, color = Color.Transparent)
                drawPath(path, color = Color.White, style = Stroke(width = 3f, cap = StrokeCap.Round))

                points.forEach { point ->
                    drawCircle(color = Color.White, radius = 12f, center = point)
                    drawCircle(color = Color.Blue, radius = 6f, center = point)
                }
            }

            Text(
                text = "Drag the corners to align the document edges",
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

private fun Offset.distanceTo(other: Offset): Float {
    return kotlin.math.hypot(x - other.x, y - other.y)
}

private fun Offset.coerceInBounds(maxWidth: Float, maxHeight: Float): Offset {
    return Offset(x.coerceIn(0f, maxWidth), y.coerceIn(0f, maxHeight))
}
