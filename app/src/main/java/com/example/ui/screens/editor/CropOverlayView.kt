package com.curiositygarage.imagekit.ui.screens.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CropOverlayView(
    cropRectNorm: Rect,
    onCropRectChange: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeHandle by remember { mutableStateOf<CropHandle?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(cropRectNorm) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val r = Rect(
                            cropRectNorm.left * w,
                            cropRectNorm.top * h,
                            cropRectNorm.right * w,
                            cropRectNorm.bottom * h
                        )
                        val touchRadius = 40.dp.toPx()

                        activeHandle = when {
                            (offset - Offset(r.left, r.top)).getDistance() < touchRadius -> CropHandle.TOP_LEFT
                            (offset - Offset(r.right, r.top)).getDistance() < touchRadius -> CropHandle.TOP_RIGHT
                            (offset - Offset(r.left, r.bottom)).getDistance() < touchRadius -> CropHandle.BOTTOM_LEFT
                            (offset - Offset(r.right, r.bottom)).getDistance() < touchRadius -> CropHandle.BOTTOM_RIGHT
                            (offset - Offset((r.left + r.right) / 2f, r.top)).getDistance() < touchRadius -> CropHandle.TOP_EDGE
                            (offset - Offset((r.left + r.right) / 2f, r.bottom)).getDistance() < touchRadius -> CropHandle.BOTTOM_EDGE
                            (offset - Offset(r.left, (r.top + r.bottom) / 2f)).getDistance() < touchRadius -> CropHandle.LEFT_EDGE
                            (offset - Offset(r.right, (r.top + r.bottom) / 2f)).getDistance() < touchRadius -> CropHandle.RIGHT_EDGE
                            r.contains(offset) -> CropHandle.BODY
                            else -> null
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val handle = activeHandle ?: return@detectDragGestures
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val dx = dragAmount.x / w
                        val dy = dragAmount.y / h

                        val newRect = when (handle) {
                            CropHandle.TOP_LEFT -> cropRectNorm.copy(
                                left = (cropRectNorm.left + dx).coerceIn(0f, cropRectNorm.right - 0.1f),
                                top = (cropRectNorm.top + dy).coerceIn(0f, cropRectNorm.bottom - 0.1f)
                            )
                            CropHandle.TOP_RIGHT -> cropRectNorm.copy(
                                right = (cropRectNorm.right + dx).coerceIn(cropRectNorm.left + 0.1f, 1f),
                                top = (cropRectNorm.top + dy).coerceIn(0f, cropRectNorm.bottom - 0.1f)
                            )
                            CropHandle.BOTTOM_LEFT -> cropRectNorm.copy(
                                left = (cropRectNorm.left + dx).coerceIn(0f, cropRectNorm.right - 0.1f),
                                bottom = (cropRectNorm.bottom + dy).coerceIn(cropRectNorm.top + 0.1f, 1f)
                            )
                            CropHandle.BOTTOM_RIGHT -> cropRectNorm.copy(
                                right = (cropRectNorm.right + dx).coerceIn(cropRectNorm.left + 0.1f, 1f),
                                bottom = (cropRectNorm.bottom + dy).coerceIn(cropRectNorm.top + 0.1f, 1f)
                            )
                            CropHandle.TOP_EDGE -> cropRectNorm.copy(
                                top = (cropRectNorm.top + dy).coerceIn(0f, cropRectNorm.bottom - 0.1f)
                            )
                            CropHandle.BOTTOM_EDGE -> cropRectNorm.copy(
                                bottom = (cropRectNorm.bottom + dy).coerceIn(cropRectNorm.top + 0.1f, 1f)
                            )
                            CropHandle.LEFT_EDGE -> cropRectNorm.copy(
                                left = (cropRectNorm.left + dx).coerceIn(0f, cropRectNorm.right - 0.1f)
                            )
                            CropHandle.RIGHT_EDGE -> cropRectNorm.copy(
                                right = (cropRectNorm.right + dx).coerceIn(cropRectNorm.left + 0.1f, 1f)
                            )
                            CropHandle.BODY -> {
                                val curW = cropRectNorm.width
                                val curH = cropRectNorm.height
                                val newL = (cropRectNorm.left + dx).coerceIn(0f, 1f - curW)
                                val newT = (cropRectNorm.top + dy).coerceIn(0f, 1f - curH)
                                Rect(newL, newT, newL + curW, newT + curH)
                            }
                        }
                        onCropRectChange(newRect)
                    },
                    onDragEnd = { activeHandle = null },
                    onDragCancel = { activeHandle = null }
                )
            }
    ) {
        val w = size.width
        val h = size.height

        val left = cropRectNorm.left * w
        val top = cropRectNorm.top * h
        val right = cropRectNorm.right * w
        val bottom = cropRectNorm.bottom * h
        val cropW = right - left
        val cropH = bottom - top

        // Dim outside crop region
        val dimColor = Color(0x99000000)
        drawRect(dimColor, Offset(0f, 0f), Size(w, top))
        drawRect(dimColor, Offset(0f, bottom), Size(w, h - bottom))
        drawRect(dimColor, Offset(0f, top), Size(left, cropH))
        drawRect(dimColor, Offset(right, top), Size(w - right, cropH))

        // Rule of thirds grid lines
        val gridColor = Color(0x66FFFFFF)
        val gridStroke = Stroke(width = 1.dp.toPx())
        val thirdW = cropW / 3f
        val thirdH = cropH / 3f

        drawLine(gridColor, Offset(left + thirdW, top), Offset(left + thirdW, bottom), strokeWidth = 1f)
        drawLine(gridColor, Offset(left + thirdW * 2, top), Offset(left + thirdW * 2, bottom), strokeWidth = 1f)
        drawLine(gridColor, Offset(left, top + thirdH), Offset(right, top + thirdH), strokeWidth = 1f)
        drawLine(gridColor, Offset(left, top + thirdH * 2), Offset(right, top + thirdH * 2), strokeWidth = 1f)

        // Main crop border
        drawRect(Color.White, Offset(left, top), Size(cropW, cropH), style = Stroke(width = 2.dp.toPx()))

        // Corner handles
        val handleLen = 22.dp.toPx()
        val handleStroke = 4.dp.toPx()
        val hColor = Color.White

        // Top Left
        drawLine(hColor, Offset(left, top), Offset(left + handleLen, top), strokeWidth = handleStroke)
        drawLine(hColor, Offset(left, top), Offset(left, top + handleLen), strokeWidth = handleStroke)

        // Top Right
        drawLine(hColor, Offset(right, top), Offset(right - handleLen, top), strokeWidth = handleStroke)
        drawLine(hColor, Offset(right, top), Offset(right, top + handleLen), strokeWidth = handleStroke)

        // Bottom Left
        drawLine(hColor, Offset(left, bottom), Offset(left + handleLen, bottom), strokeWidth = handleStroke)
        drawLine(hColor, Offset(left, bottom), Offset(left, bottom - handleLen), strokeWidth = handleStroke)

        // Bottom Right
        drawLine(hColor, Offset(right, bottom), Offset(right - handleLen, bottom), strokeWidth = handleStroke)
        drawLine(hColor, Offset(right, bottom), Offset(right, bottom - handleLen), strokeWidth = handleStroke)
    }
}

private enum class CropHandle {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
    TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, RIGHT_EDGE,
    BODY
}
