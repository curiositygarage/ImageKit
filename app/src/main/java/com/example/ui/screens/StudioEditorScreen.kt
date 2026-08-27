package com.curiositygarage.imagekit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.ui.components.IosCapsuleButton
import com.curiositygarage.imagekit.ui.screens.editor.AdjustmentSliderPanel
import com.curiositygarage.imagekit.ui.screens.editor.AspectRatioPicker
import com.curiositygarage.imagekit.ui.screens.editor.CompareToggle
import com.curiositygarage.imagekit.ui.screens.editor.EditorPreviewCanvas
import com.curiositygarage.imagekit.ui.screens.editor.ExportOptionsPanel
import com.curiositygarage.imagekit.ui.screens.editor.FilterStrip
import com.curiositygarage.imagekit.ui.screens.editor.MarkupToolbar
import com.curiositygarage.imagekit.ui.theme.IosCardSurface
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosSystemYellow
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioEditorScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
    ) {
        // iOS Photos-style Top Bar
        StudioTopBar(
            canUndo = uiState.canUndo,
            canRedo = uiState.canRedo,
            onUndo = { viewModel.undo() },
            onRedo = { viewModel.redo() },
            onRevert = { viewModel.resetAllEdits() },
            isComparing = uiState.showCompare,
            onCompareChange = { viewModel.setCompareOriginal(it) },
            onSave = { viewModel.saveEditedImage() },
            isProcessing = uiState.isProcessing
        )

        // Main Studio Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            EditorPreviewCanvas(
                displayBitmap = uiState.previewBitmap,
                originalBitmap = uiState.originalBitmap,
                showCompare = uiState.showCompare,
                activeTool = uiState.activeTool,
                cropRectNorm = uiState.cropRectNorm,
                onCropRectChange = { viewModel.updateCropRect(it) },
                activeMarkupType = uiState.activeMarkupType,
                onAddMarkup = { viewModel.addMarkup(it) }
            )

            if (uiState.isLoading || uiState.isProcessing) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = IosSystemBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }
        }

        // Apple Photos-style Bottom Tool Dock
        StudioToolDock(
            activeTool = uiState.activeTool,
            onToolSelected = { tool ->
                if (tool == EditTool.AUTO_ENHANCE) {
                    viewModel.setFilter(FilterPreset.AUTO_ENHANCE)
                } else {
                    viewModel.setActiveTool(if (uiState.activeTool == tool) null else tool)
                }
            }
        )
    }

    // iOS-Style Modal Bottom Sheet for Active Tools
    if (uiState.activeTool != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setActiveTool(null) },
            sheetState = sheetState,
            containerColor = Color(0xF21C1C1E),
            scrimColor = Color.Black.copy(alpha = 0.4f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .width(36.dp)
                            .height(5.dp),
                        shape = CircleShape,
                        color = IosLabelTertiary
                    ) {}
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.activeTool?.label ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = IosLabelPrimary
                    )

                    IosCapsuleButton(
                        text = "Done",
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setActiveTool(null)
                        },
                        isPrimary = false
                    )
                }

                // Tool-specific options view inside iOS Bottom Sheet
                when (uiState.activeTool) {
                    EditTool.RESIZE_CONVERT -> {
                        ExportOptionsPanel(
                            selectedWebPreset = uiState.selectedWebPreset,
                            onApplyWebPreset = { viewModel.applyWebUploadPreset(it) },
                            selectedTargetBytes = uiState.selectedTargetBytes,
                            onTargetBytesSelected = { viewModel.setTargetByteSize(it) },
                            isCustomDimensionMode = uiState.isCustomDimensionMode,
                            onCustomDimensionModeToggle = { viewModel.setCustomDimensionMode(it) },
                            customWidth = uiState.customWidth,
                            customHeight = uiState.customHeight,
                            maintainAspectRatio = uiState.maintainAspectRatio,
                            onCustomDimensionsChange = { w, h, keep -> viewModel.setCustomDimensions(w, h, keep) },
                            resizeScale = uiState.resizeScale,
                            onResizeScaleChange = { viewModel.setResizeScale(it) },
                            exportFormat = uiState.exportFormat,
                            onExportFormatChange = { viewModel.setExportFormat(it) },
                            exportQuality = uiState.exportQuality,
                            onExportQualityChange = { viewModel.setExportQuality(it) },
                            estimatedSizeBytes = uiState.estimatedSizeBytes,
                            originalSizeBytes = uiState.originalEstimatedSizeBytes
                        )
                    }
                    EditTool.CROP_ROTATE -> {
                        AspectRatioPicker(
                            selectedRatio = uiState.selectedAspectRatio,
                            onRatioSelected = { viewModel.setAspectRatio(it) },
                            onRotate = { viewModel.rotate(it) },
                            onFlip = { h, v -> viewModel.flip(h, v) },
                            onApplyCrop = {
                                viewModel.applyCrop()
                                viewModel.setActiveTool(null)
                            }
                        )
                    }
                    EditTool.FILTERS -> {
                        FilterStrip(
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelected = { viewModel.setFilter(it) }
                        )
                    }
                    EditTool.ADJUST -> {
                        AdjustmentSliderPanel(
                            adjustments = uiState.adjustments,
                            onAdjustmentsChanged = { viewModel.updateAdjustments(it) },
                            onResetAdjustments = { viewModel.updateAdjustments(com.curiositygarage.imagekit.model.Adjustments()) }
                        )
                    }
                    EditTool.REDACT_MARKUP -> {
                        MarkupToolbar(
                            activeMarkupType = uiState.activeMarkupType,
                            onMarkupTypeSelected = { viewModel.setActiveMarkupType(it) },
                            markups = uiState.markups,
                            onRemoveMarkup = { viewModel.removeMarkup(it) },
                            onClearMarkups = { viewModel.clearMarkups() }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onRevert: () -> Unit,
    isComparing: Boolean,
    onCompareChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    isProcessing: Boolean
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = IosSecondarySystemBackgroundDark,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Undo / Redo / Revert
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onUndo()
                    },
                    enabled = canUndo,
                    modifier = Modifier.size(36.dp).testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (canUndo) IosSystemYellow else IosLabelSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRedo()
                    },
                    enabled = canRedo,
                    modifier = Modifier.size(36.dp).testTag("redo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (canRedo) IosSystemYellow else IosLabelSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                TextButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRevert()
                    },
                    modifier = Modifier.testTag("revert_button")
                ) {
                    Text(
                        text = stringResource(R.string.action_revert),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = IosSystemYellow
                    )
                }
            }

            // Right: Compare & Save Capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompareToggle(
                    isComparing = isComparing,
                    onCompareChange = onCompareChange
                )

                IosCapsuleButton(
                    text = stringResource(R.string.action_save),
                    onClick = onSave,
                    isPrimary = true,
                    testTag = "save_image_button"
                )
            }
        }
    }
}

@Composable
private fun StudioToolDock(
    activeTool: EditTool?,
    onToolSelected: (EditTool) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = IosSecondarySystemBackgroundDark,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(EditTool.entries) { tool ->
                val isSelected = activeTool == tool
                val icon: ImageVector = when (tool) {
                    EditTool.RESIZE_CONVERT -> Icons.Default.Compress
                    EditTool.CROP_ROTATE -> Icons.Default.Crop
                    EditTool.FILTERS -> Icons.Default.AutoAwesome
                    EditTool.AUTO_ENHANCE -> Icons.Default.AutoFixHigh
                    EditTool.ADJUST -> Icons.Default.Tune
                    EditTool.REDACT_MARKUP -> Icons.Default.Brush
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToolSelected(tool)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("tool_dock_${tool.name.lowercase()}")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) IosSystemYellow else IosTertiarySystemBackgroundDark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tool.label,
                                tint = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (tool) {
                            EditTool.RESIZE_CONVERT -> "Optimize"
                            EditTool.CROP_ROTATE -> "Crop"
                            EditTool.FILTERS -> "Filters"
                            EditTool.AUTO_ENHANCE -> "Auto"
                            EditTool.ADJUST -> "Adjust"
                            EditTool.REDACT_MARKUP -> "Markup"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = (-0.1).sp
                        ),
                        color = if (isSelected) IosSystemYellow else IosLabelSecondary
                    )
                }
            }
        }
    }
}
