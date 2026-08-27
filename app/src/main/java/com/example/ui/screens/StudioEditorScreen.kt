package com.curiositygarage.imagekit.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.model.AspectRatioPreset
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.model.MarkupItem
import com.curiositygarage.imagekit.model.MarkupType
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.model.TARGET_SIZE_PRESETS
import com.curiositygarage.imagekit.model.WebUploadPreset
import com.curiositygarage.imagekit.ui.components.AdjustmentSlider
import com.curiositygarage.imagekit.ui.components.FilterChipItem
import com.curiositygarage.imagekit.ui.theme.ElegantBackground
import com.curiositygarage.imagekit.ui.theme.ElegantOnPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantOutline
import com.curiositygarage.imagekit.ui.theme.ElegantPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantSurface
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceContainer
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceContainerHigh
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceVariant
import com.curiositygarage.imagekit.ui.theme.ElegantTextPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantTextSecondary
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@Composable
fun StudioEditorScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    var comparePressed by remember { mutableStateOf(false) }
    val displayBitmap = if (comparePressed) uiState.originalBitmap else uiState.previewBitmap
    val activeTool = uiState.activeTool ?: EditTool.CROP_ROTATE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantBackground)
    ) {
        // --- Top Bar Actions ---
        StudioTopBar(
            canUndo = uiState.canUndo,
            canRedo = uiState.canRedo,
            isSaving = uiState.isProcessing,
            onBack = { viewModel.setTab(NavigationTab.HOME) },
            onUndo = { viewModel.undo() },
            onRedo = { viewModel.redo() },
            onReset = { viewModel.resetAllEdits() },
            onSave = { viewModel.saveEditedImage() },
            onCompareChanged = { comparePressed = it }
        )

        // --- Main Canvas Viewport ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(ElegantSurfaceContainer)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (displayBitmap != null) {
                val imgRatio = displayBitmap.width.toFloat() / displayBitmap.height.toFloat()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imgRatio.coerceIn(0.5f, 2.2f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, ElegantOutline, RoundedCornerShape(12.dp))
                        .pointerInput(activeTool) {
                            if (activeTool == EditTool.REDACT_MARKUP) {
                                detectTapGestures { offset ->
                                    val normX = (offset.x / size.width).coerceIn(0f, 0.9f)
                                    val normY = (offset.y / size.height).coerceIn(0f, 0.9f)
                                    val newItem = MarkupItem(
                                        type = uiState.activeMarkupType,
                                        rect = Rect(normX, normY, normX + 0.35f, normY + 0.12f),
                                        startOffset = Offset(normX, normY),
                                        endOffset = Offset(normX + 0.25f, normY + 0.15f),
                                        text = if (uiState.activeMarkupType == MarkupType.TEXT) "NOTE" else ""
                                    )
                                    viewModel.addMarkup(newItem)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = displayBitmap.asImageBitmap(),
                        contentDescription = "Editing Canvas",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("editor_canvas_image")
                    )

                    // Visual Crop Frame Overlay when Crop tool is selected
                    if (activeTool == EditTool.CROP_ROTATE && !comparePressed) {
                        CropOverlay(cropRect = uiState.cropRectNorm)
                    }

                    // Comparison indicator badge
                    if (comparePressed) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        ) {
                            Text(
                                text = "ORIGINAL IMAGE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ElegantPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Info overlay chip (Dimensions & estimated file size)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElegantSurface.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${displayBitmap.width} × ${displayBitmap.height} px",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantTextSecondary
                        )
                        Text(
                            text = "${uiState.exportFormat.extension.uppercase()} ~${formatBytes(uiState.estimatedSizeBytes)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElegantPrimary
                        )
                    }
                }
            } else {
                CircularProgressIndicator(color = ElegantPrimary)
            }
        }

        // --- Tool Selection Shelf & Controls ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ElegantSurface)
                .border(
                    width = 1.dp,
                    color = ElegantOutline,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(top = 8.dp, bottom = 12.dp)
        ) {
            // Horizontal Tool Categories Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditTool.entries.forEach { tool ->
                    val isSelected = activeTool == tool
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.setActiveTool(tool) }
                            .testTag("tool_tab_${tool.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ElegantPrimary else ElegantSurfaceVariant,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tool.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tool.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) ElegantOnPrimary else ElegantTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tool-Specific Control Drawer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (activeTool) {
                    EditTool.CROP_ROTATE -> CropRotateControls(viewModel, uiState)
                    EditTool.AUTO_ENHANCE -> AutoEnhanceControls(viewModel, uiState)
                    EditTool.FILTERS -> FilterControls(viewModel, uiState)
                    EditTool.ADJUST -> AdjustmentControls(viewModel, uiState)
                    EditTool.REDACT_MARKUP -> RedactMarkupControls(viewModel, uiState)
                    EditTool.RESIZE_CONVERT -> ResizeConvertControls(viewModel, uiState)
                }
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
    onCompareChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElegantBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("studio_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ElegantTextPrimary
                )
            }

            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.testTag("undo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) ElegantTextPrimary else ElegantTextSecondary.copy(alpha = 0.4f)
                )
            }

            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.testTag("redo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) ElegantTextPrimary else ElegantTextSecondary.copy(alpha = 0.4f)
                )
            }

            IconButton(
                onClick = onReset,
                modifier = Modifier.testTag("reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = ElegantTextSecondary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Compare button (press to hold)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onCompareChanged(true)
                                tryAwaitRelease()
                                onCompareChanged(false)
                            }
                        )
                    }
                    .testTag("compare_button"),
                shape = RoundedCornerShape(10.dp),
                color = ElegantSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Compare",
                        tint = ElegantPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hold Compare",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = ElegantPrimary
                    )
                }
            }

            // Save pill
            Button(
                onClick = onSave,
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantPrimary,
                    contentColor = ElegantOnPrimary
                ),
                modifier = Modifier.testTag("save_image_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = ElegantOnPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun CropRotateControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick Orient Actions: 90 Rotate & Flips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = "⟳ +90°",
                label = "Rotate",
                onClick = { viewModel.rotate(90f) },
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = "⇄",
                label = "Flip H",
                onClick = { viewModel.flip(horizontal = true, vertical = false) },
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = "⇅",
                label = "Flip V",
                onClick = { viewModel.flip(horizontal = false, vertical = true) },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { viewModel.applyCrop() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantPrimary,
                    contentColor = ElegantOnPrimary
                ),
                modifier = Modifier.weight(1.2f).testTag("apply_crop_button")
            ) {
                Text(text = "Apply Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Aspect Ratio presets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AspectRatioPreset.entries.forEach { preset ->
                val selected = uiState.selectedAspectRatio == preset
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.setAspectRatio(preset) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else ElegantSurfaceVariant,
                    border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary) else null
                ) {
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selected) ElegantPrimary else ElegantTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoEnhanceControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "One-Tap Auto Clarity",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ElegantTextPrimary
                )
                Text(
                    text = "Balances desk shadows, boosts contrast and color vibrancy",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ElegantTextSecondary
                )
            }

            Button(
                onClick = { viewModel.setFilter(FilterPreset.AUTO_ENHANCE) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.selectedFilter == FilterPreset.AUTO_ENHANCE) ElegantPrimary else ElegantSurfaceVariant,
                    contentColor = if (uiState.selectedFilter == FilterPreset.AUTO_ENHANCE) ElegantOnPrimary else ElegantTextPrimary
                ),
                modifier = Modifier.testTag("apply_auto_enhance_button")
            ) {
                Text(
                    text = if (uiState.selectedFilter == FilterPreset.AUTO_ENHANCE) "Active ✨" else "Enhance ✨",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = "🧾",
                label = "Clean Document",
                onClick = { viewModel.setFilter(FilterPreset.DOC_CLEAN) },
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = "📋",
                label = "Whiteboard Pop",
                onClick = { viewModel.setFilter(FilterPreset.WHITEBOARD) },
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = "↺",
                label = "Revert",
                onClick = { viewModel.setFilter(FilterPreset.ORIGINAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "PRESET FILTERS & SCANNERS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            ),
            color = ElegantTextSecondary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterPreset.entries.forEach { preset ->
                FilterChipItem(
                    label = preset.label,
                    selected = uiState.selectedFilter == preset,
                    onClick = { viewModel.setFilter(preset) }
                )
            }
        }
    }
}

@Composable
private fun AdjustmentControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    val adj = uiState.adjustments
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AdjustmentSlider(
            label = "Brightness",
            value = adj.brightness,
            range = -100f..100f,
            valueLabel = "${adj.brightness.toInt()}",
            onValueChange = { viewModel.updateAdjustments(adj.copy(brightness = it)) }
        )
        AdjustmentSlider(
            label = "Contrast",
            value = adj.contrast,
            range = -100f..100f,
            valueLabel = "${adj.contrast.toInt()}",
            onValueChange = { viewModel.updateAdjustments(adj.copy(contrast = it)) }
        )
        AdjustmentSlider(
            label = "Saturation",
            value = adj.saturation,
            range = -100f..100f,
            valueLabel = "${adj.saturation.toInt()}",
            onValueChange = { viewModel.updateAdjustments(adj.copy(saturation = it)) }
        )
        AdjustmentSlider(
            label = "Warmth / Tint",
            value = adj.warmth,
            range = -100f..100f,
            valueLabel = "${adj.warmth.toInt()}",
            onValueChange = { viewModel.updateAdjustments(adj.copy(warmth = it)) }
        )
        AdjustmentSlider(
            label = "Vignette Framing",
            value = adj.vignette,
            range = 0f..100f,
            valueLabel = "${adj.vignette.toInt()}%",
            onValueChange = { viewModel.updateAdjustments(adj.copy(vignette = it)) }
        )
    }
}

@Composable
private fun RedactMarkupControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TAP IMAGE TO ADD REDACTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                ),
                color = ElegantPrimary
            )

            if (uiState.markups.isNotEmpty()) {
                Text(
                    text = "Clear (${uiState.markups.size})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFFB4AB),
                    modifier = Modifier
                        .clickable { viewModel.clearMarkups() }
                        .padding(4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MarkupType.entries.forEach { type ->
                val isSelected = uiState.activeMarkupType == type
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.setActiveMarkupType(type) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else ElegantSurfaceVariant,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary) else null
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = type.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = type.label.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isSelected) ElegantPrimary else ElegantTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResizeConvertControls(viewModel: ImageEditorViewModel, uiState: EditorUiState) {
    var showCustomDimensions by remember { mutableStateOf(uiState.isCustomDimensionMode) }
    var widthText by remember(uiState.customWidth) { mutableStateOf(if (uiState.customWidth > 0) uiState.customWidth.toString() else "") }
    var heightText by remember(uiState.customHeight) { mutableStateOf(if (uiState.customHeight > 0) uiState.customHeight.toString() else "") }
    var maintainAspect by remember { mutableStateOf(uiState.maintainAspectRatio) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Live Size & Optimization Reduction Badge
        val origBytes = uiState.originalEstimatedSizeBytes
        val optBytes = uiState.estimatedSizeBytes
        val savingsPct = if (origBytes > 0 && optBytes < origBytes) {
            ((origBytes - optBytes).toFloat() / origBytes * 100).toInt()
        } else 0

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = ElegantSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (savingsPct > 0) ElegantPrimary.copy(alpha = 0.5f) else ElegantOutline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ESTIMATED OUTPUT SIZE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            fontSize = 9.sp
                        ),
                        color = ElegantTextSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatBytes(optBytes),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = ElegantPrimary
                        )
                        if (origBytes > 0) {
                            Text(
                                text = "from ${formatBytes(origBytes)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ElegantTextSecondary
                            )
                        }
                    }
                }

                if (savingsPct > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElegantPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "⚡ $savingsPct% smaller",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = ElegantPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Web / HTML Upload Presets (Passport, Web Form, Search Thumb, Desktop FHD)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "HTML & WEB UPLOAD PRESETS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                ),
                color = ElegantTextSecondary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WebUploadPreset.entries.forEach { preset ->
                    val isSelected = uiState.selectedWebPreset == preset
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.applyWebUploadPreset(preset) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else ElegantSurfaceVariant,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = preset.icon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) ElegantPrimary else ElegantTextPrimary
                                )
                            }
                            Text(
                                text = "${preset.targetWidth ?: "Auto"}×${preset.targetHeight ?: "Auto"} • ${preset.subtitle}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                color = ElegantTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Strict Target File Size Auto-Compressor Chips
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUTO-FIT TARGET SIZE BUDGET",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = ElegantTextSecondary
                )
                if (uiState.selectedTargetBytes != null) {
                    Text(
                        text = "Clear Limit",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFB4AB),
                        modifier = Modifier.clickable { viewModel.setTargetByteSize(null) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TARGET_SIZE_PRESETS.forEach { preset ->
                    val isSelected = uiState.selectedTargetBytes == preset.bytes
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setTargetByteSize(preset.bytes) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ElegantPrimary else ElegantSurfaceVariant
                    ) {
                        Text(
                            text = preset.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) ElegantOnPrimary else ElegantTextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 4. Custom Pixel Dimensions or Scale Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DIMENSIONS & SCALE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                ),
                color = ElegantTextSecondary
            )

            Text(
                text = if (showCustomDimensions) "Use % Scale" else "Custom Px Size",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = ElegantPrimary,
                modifier = Modifier
                    .clickable {
                        showCustomDimensions = !showCustomDimensions
                        viewModel.setCustomDimensionMode(showCustomDimensions)
                    }
                    .padding(4.dp)
            )
        }

        if (showCustomDimensions) {
            // Width and Height Text inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = widthText,
                    onValueChange = {
                        widthText = it.filter { ch -> ch.isDigit() }
                        val w = widthText.toIntOrNull() ?: 0
                        val h = heightText.toIntOrNull() ?: 0
                        if (w > 0) {
                            viewModel.setCustomDimensions(w, if (h <= 0) w else h, maintainAspect)
                        }
                    },
                    label = { Text("Width px", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantPrimary,
                        unfocusedBorderColor = ElegantOutline,
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Text("×", color = ElegantTextSecondary, fontSize = 16.sp)

                OutlinedTextField(
                    value = heightText,
                    onValueChange = {
                        heightText = it.filter { ch -> ch.isDigit() }
                        val w = widthText.toIntOrNull() ?: 0
                        val h = heightText.toIntOrNull() ?: 0
                        if (h > 0) {
                            viewModel.setCustomDimensions(if (w <= 0) h else w, h, maintainAspect)
                        }
                    },
                    label = { Text("Height px", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantPrimary,
                        unfocusedBorderColor = ElegantOutline,
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick resolution fill buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "600×600 (Passport)" to (600 to 600),
                    "800×600 (Web)" to (800 to 600),
                    "1200×800 (Article)" to (1200 to 800),
                    "1920×1080 (FHD)" to (1920 to 1080)
                ).forEach { (label, dims) ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                widthText = dims.first.toString()
                                heightText = dims.second.toString()
                                viewModel.setCustomDimensions(dims.first, dims.second, maintainAspect)
                            },
                        shape = RoundedCornerShape(6.dp),
                        color = ElegantSurfaceVariant
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ElegantTextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Scale Percentage Presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ResizeScale.entries.forEach { scale ->
                    val selected = uiState.resizeScale == scale && !uiState.isCustomDimensionMode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showCustomDimensions = false
                                viewModel.setResizeScale(scale)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else ElegantSurfaceVariant,
                        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary) else null
                    ) {
                        Text(
                            text = scale.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = if (selected) ElegantPrimary else ElegantTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 5. Output Format (WEBP, JPEG, PNG)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OUTPUT FORMAT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    ),
                    color = ElegantTextSecondary
                )
                Text(
                    text = if (uiState.exportFormat == ExportFormat.WEBP) "WebP provides smallest web file size" else "",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                    color = ElegantPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ExportFormat.entries.forEach { format ->
                    val selected = uiState.exportFormat == format
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setExportFormat(format) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) ElegantPrimary else ElegantSurfaceVariant
                    ) {
                        Text(
                            text = format.extension.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selected) ElegantOnPrimary else ElegantTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 6. Quality Slider
        AdjustmentSlider(
            label = "Compression Quality",
            value = uiState.exportQuality.toFloat(),
            range = 20f..100f,
            valueLabel = "${uiState.exportQuality}%",
            onValueChange = { viewModel.setExportQuality(it.toInt()) }
        )
    }
}

@Composable
private fun ActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = ElegantSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ElegantTextPrimary
            )
        }
    }
}

@Composable
private fun CropOverlay(cropRect: Rect) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, ElegantPrimary.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
    )
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.2f MB", bytes.toDouble() / (1024 * 1024))
    }
}
