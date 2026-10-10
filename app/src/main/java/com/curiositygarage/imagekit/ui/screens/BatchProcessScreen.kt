package com.curiositygarage.imagekit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.ui.components.IosCapsuleButton
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.components.IosIconBadge
import com.curiositygarage.imagekit.ui.components.IosLargeNavigationBar
import com.curiositygarage.imagekit.ui.components.IosSegmentedControl
import com.curiositygarage.imagekit.ui.theme.IosCardSurface
import com.curiositygarage.imagekit.ui.theme.IosFillTertiary
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosSystemGreen
import com.curiositygarage.imagekit.ui.theme.IosSystemIndigo
import com.curiositygarage.imagekit.ui.theme.IosSystemRed
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@Composable
fun BatchProcessScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var selectedScale by remember { mutableStateOf(ResizeScale.SCALE_50) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.WEBP) }
    var quality by remember { mutableIntStateOf(85) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
            .padding(bottom = 16.dp)
    ) {
        IosLargeNavigationBar(
            title = stringResource(R.string.batch_title),
            subtitle = stringResource(R.string.batch_subtitle),
            actionButton = {
                IosCapsuleButton(
                    text = stringResource(R.string.batch_add_samples),
                    icon = Icons.Default.Add,
                    onClick = { viewModel.addBatchSampleImages() },
                    isPrimary = false,
                    testTag = "batch_add_samples_btn"
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Batch Output Settings
            item {
                IosGroupedCard(header = stringResource(R.string.batch_settings_title)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Resolution Scale Segmented Control
                        Column {
                            Text(
                                text = stringResource(R.string.batch_scale_label),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = IosLabelSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            IosSegmentedControl(
                                items = listOf(ResizeScale.SCALE_75, ResizeScale.SCALE_50, ResizeScale.SCALE_25, ResizeScale.HD_1080),
                                selectedItem = selectedScale,
                                onItemSelected = { selectedScale = it },
                                labelProvider = { it.label.split(" ").first() }
                            )
                        }

                        // Target File Format Segmented Control
                        Column {
                            Text(
                                text = stringResource(R.string.batch_format_label),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = IosLabelSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            IosSegmentedControl(
                                items = ExportFormat.entries,
                                selectedItem = selectedFormat,
                                onItemSelected = { selectedFormat = it },
                                labelProvider = { it.extension.uppercase() }
                            )
                        }

                        // Quality Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.batch_quality_label),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = IosLabelSecondary
                                )
                                Text(
                                    text = "$quality%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = IosSystemBlue
                                )
                            }
                            Slider(
                                value = quality.toFloat(),
                                onValueChange = { quality = it.toInt() },
                                valueRange = 30f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = IosSystemBlue,
                                    inactiveTrackColor = IosFillTertiary
                                )
                            )
                        }
                    }
                }
            }

            // Queue status and start button
            item {
                if (uiState.batchBitmaps.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = IosCardSurface,
                        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.batch_queue_count, uiState.batchBitmaps.size),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = IosLabelPrimary
                                )
                                TextButton(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.clearBatch()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.batch_clear_queue),
                                        color = IosSystemRed,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }

                            if (uiState.isBatchProcessing) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { uiState.batchProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = IosSystemGreen,
                                    trackColor = IosTertiarySystemBackgroundDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(
                                        R.string.batch_processing_status,
                                        (uiState.batchProgress * uiState.batchBitmaps.size).toInt() + 1,
                                        uiState.batchBitmaps.size
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IosLabelSecondary
                                )
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.runBatchConvert(selectedScale, selectedFormat, quality)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IosSystemBlue),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("start_batch_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.batch_start_button),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.1).sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Queue Items or Empty Placeholder
            if (uiState.batchBitmaps.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = IosCardSurface,
                        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            IosIconBadge(
                                icon = Icons.Default.Layers,
                                backgroundColor = IosSystemIndigo,
                                size = 52.dp,
                                iconSize = 28.dp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = stringResource(R.string.batch_empty_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp,
                                    letterSpacing = (-0.2).sp
                                ),
                                color = IosLabelPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.batch_empty_desc),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    letterSpacing = (-0.1).sp
                                ),
                                color = IosLabelSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(uiState.batchBitmaps) { (name, bitmap) ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = IosCardSurface,
                        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name.replace("_", " "),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = IosLabelPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${bitmap.width}×${bitmap.height} px",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                    color = IosLabelSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
