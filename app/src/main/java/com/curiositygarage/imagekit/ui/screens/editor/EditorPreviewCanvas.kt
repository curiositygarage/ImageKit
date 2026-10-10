package com.curiositygarage.imagekit.ui.screens.editor

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.MarkupItem
import com.curiositygarage.imagekit.model.MarkupType
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosSystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue

@Composable
fun EditorPreviewCanvas(
    displayBitmap: Bitmap?,
    originalBitmap: Bitmap?,
    showCompare: Boolean,
    activeTool: EditTool?,
    cropRectNorm: Rect,
    onCropRectChange: (Rect) -> Unit,
    activeMarkupType: MarkupType,
    onAddMarkup: (MarkupItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
            .clipToBounds()
    ) {
        val containerWidth = maxWidth.value
        val containerHeight = maxHeight.value

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(activeTool) {
                    if (activeTool != EditTool.CROP_ROTATE) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.8f, 5.0f)
                            panOffset = if (zoomScale > 1f) {
                                Offset(panOffset.x + pan.x, panOffset.y + pan.y)
                            } else {
                                Offset.Zero
                            }
                        }
                    }
                }
                .pointerInput(activeTool, activeMarkupType) {
                    if (activeTool == EditTool.REDACT_MARKUP) {
                        detectTapGestures { tapOffset ->
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val normX = (tapOffset.x / size.width).coerceIn(0.05f, 0.8f)
                            val normY = (tapOffset.y / size.height).coerceIn(0.05f, 0.8f)

                            val newItem = when (activeMarkupType) {
                                MarkupType.ARROW -> MarkupItem(
                                    type = MarkupType.ARROW,
                                    startOffset = Offset(normX, normY),
                                    endOffset = Offset(normX + 0.15f, normY + 0.12f)
                                )
                                MarkupType.TEXT -> MarkupItem(
                                    type = MarkupType.TEXT,
                                    rect = Rect(normX, normY, normX + 0.3f, normY + 0.08f),
                                    text = "CONFIDENTIAL"
                                )
                                else -> MarkupItem(
                                    type = activeMarkupType,
                                    rect = Rect(normX, normY, normX + 0.25f, normY + 0.12f)
                                )
                            }
                            onAddMarkup(newItem)
                        }
                    } else if (activeTool != EditTool.CROP_ROTATE) {
                        detectTapGestures(
                            onDoubleTap = {
                                zoomScale = if (zoomScale > 1.2f) 1f else 2.5f
                                panOffset = Offset.Zero
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val currentImage = if (showCompare && originalBitmap != null) originalBitmap else displayBitmap

            if (currentImage != null) {
                Crossfade(targetState = showCompare, label = "compare_fade") { comparing ->
                    val bmp = if (comparing && originalBitmap != null) originalBitmap else currentImage
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Studio Canvas",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .graphicsLayer(
                                scaleX = zoomScale,
                                scaleY = zoomScale,
                                translationX = panOffset.x,
                                translationY = panOffset.y
                            )
                            .testTag("studio_canvas_image"),
                        contentScale = ContentScale.Fit
                    )
                }

                // Interactive 8-handle Crop Overlay
                if (activeTool == EditTool.CROP_ROTATE && !showCompare) {
                    CropOverlayView(
                        cropRectNorm = cropRectNorm,
                        onCropRectChange = onCropRectChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                // Comparison Mode Watermark Pill
                if (showCompare) {
                    Surface(
                        shape = CircleShape,
                        color = IosSystemBlue.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.compare_original_label),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Resolution Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "${currentImage.width}×${currentImage.height} px",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = IosLabelPrimary.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
