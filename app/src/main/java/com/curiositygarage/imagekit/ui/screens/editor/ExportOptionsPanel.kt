package com.curiositygarage.imagekit.ui.screens.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.model.TARGET_SIZE_PRESETS
import com.curiositygarage.imagekit.model.WebUploadPreset
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.components.IosListRow
import com.curiositygarage.imagekit.ui.components.IosSegmentedControl
import com.curiositygarage.imagekit.ui.theme.IosFillTertiary
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosSystemGreen
import com.curiositygarage.imagekit.ui.theme.IosSystemOrange
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark

@Composable
fun ExportOptionsPanel(
    selectedWebPreset: WebUploadPreset?,
    onApplyWebPreset: (WebUploadPreset) -> Unit,
    selectedTargetBytes: Long?,
    onTargetBytesSelected: (Long?) -> Unit,
    isCustomDimensionMode: Boolean,
    onCustomDimensionModeToggle: (Boolean) -> Unit,
    customWidth: Int,
    customHeight: Int,
    maintainAspectRatio: Boolean,
    onCustomDimensionsChange: (Int, Int, Boolean) -> Unit,
    resizeScale: ResizeScale,
    onResizeScaleChange: (ResizeScale) -> Unit,
    exportFormat: ExportFormat,
    onExportFormatChange: (ExportFormat) -> Unit,
    exportQuality: Int,
    onExportQualityChange: (Int) -> Unit,
    estimatedSizeBytes: Long,
    originalSizeBytes: Long,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Size Estimation Badge
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = IosSecondarySystemBackgroundDark,
            border = BorderStroke(0.5.dp, IosSeparatorOpaque)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.export_estimated_size).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = IosLabelTertiary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatBytes(estimatedSizeBytes),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = IosSystemGreen
                    )
                }

                if (originalSizeBytes > 0) {
                    val savings = if (originalSizeBytes > estimatedSizeBytes) {
                        ((originalSizeBytes - estimatedSizeBytes) * 100 / originalSizeBytes).toInt()
                    } else 0
                    Surface(
                        shape = CircleShape,
                        color = if (savings > 0) IosSystemGreen.copy(alpha = 0.15f) else IosTertiarySystemBackgroundDark
                    ) {
                        Text(
                            text = if (savings > 0) "-$savings% smaller" else "Original: ${formatBytes(originalSizeBytes)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (savings > 0) IosSystemGreen else IosLabelSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Web & Form Preset Quick Pills
        IosGroupedCard(header = stringResource(R.string.export_web_presets_header)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WebUploadPreset.entries) { preset ->
                    val isSelected = selectedWebPreset == preset
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onApplyWebPreset(preset)
                            }
                            .testTag("web_preset_${preset.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IosSystemBlue else IosTertiarySystemBackgroundDark,
                        border = if (!isSelected) BorderStroke(0.5.dp, IosSeparatorOpaque) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    letterSpacing = (-0.1).sp
                                ),
                                color = if (isSelected) Color.White else IosLabelPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = preset.subtitle.split("•").first().trim(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else IosLabelSecondary
                            )
                        }
                    }
                }
            }
        }

        // Target File Size Mode
        IosGroupedCard(
            header = stringResource(R.string.export_target_size_header),
            footer = stringResource(R.string.export_target_size_footer)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TARGET_SIZE_PRESETS) { target ->
                    val isSelected = selectedTargetBytes == target.bytes
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTargetBytesSelected(if (isSelected) null else target.bytes)
                            }
                            .testTag("target_bytes_${target.bytes}"),
                        shape = CircleShape,
                        color = if (isSelected) IosSystemGreen else IosTertiarySystemBackgroundDark,
                        border = if (!isSelected) BorderStroke(0.5.dp, IosSeparatorOpaque) else null
                    ) {
                        Text(
                            text = target.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = if (isSelected) Color.White else IosLabelPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Export Format Segmented Control
        IosGroupedCard(header = stringResource(R.string.export_format_header)) {
            Column(modifier = Modifier.padding(12.dp)) {
                IosSegmentedControl(
                    items = ExportFormat.entries,
                    selectedItem = exportFormat,
                    onItemSelected = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onExportFormatChange(it)
                    },
                    labelProvider = { it.label }
                )
            }
        }

        // Resolution Scale Segmented Control
        IosGroupedCard(header = stringResource(R.string.export_scale_header)) {
            Column(modifier = Modifier.padding(12.dp)) {
                IosSegmentedControl(
                    items = listOf(ResizeScale.SCALE_75, ResizeScale.SCALE_50, ResizeScale.SCALE_25, ResizeScale.HD_1080),
                    selectedItem = resizeScale,
                    onItemSelected = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onResizeScaleChange(it)
                    },
                    labelProvider = { it.label.split(" ").first() }
                )
            }
        }

        // Quality Slider
        if (exportFormat != ExportFormat.PNG) {
            IosGroupedCard(header = stringResource(R.string.export_quality_header)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.export_quality_compression),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = IosLabelPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = IosTertiarySystemBackgroundDark
                        ) {
                            Text(
                                text = "$exportQuality%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IosSystemBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Slider(
                        value = exportQuality.toFloat(),
                        onValueChange = { onExportQualityChange(it.toInt()) },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = IosSystemBlue,
                            inactiveTrackColor = IosFillTertiary
                        ),
                        modifier = Modifier.testTag("export_quality_slider")
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 KB"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format(java.util.Locale.US, "%.2f MB", mb)
    } else {
        String.format(java.util.Locale.US, "%.1f KB", kb)
    }
}
