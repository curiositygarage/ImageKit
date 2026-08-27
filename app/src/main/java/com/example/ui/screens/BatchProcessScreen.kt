package com.curiositygarage.imagekit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.model.ExportFormat
import com.curiositygarage.imagekit.model.ResizeScale
import com.curiositygarage.imagekit.ui.components.AdjustmentSlider
import com.curiositygarage.imagekit.ui.components.AppHeader
import com.curiositygarage.imagekit.ui.theme.ElegantBackground
import com.curiositygarage.imagekit.ui.theme.ElegantOnPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantOutline
import com.curiositygarage.imagekit.ui.theme.ElegantPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantSurface
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceVariant
import com.curiositygarage.imagekit.ui.theme.ElegantTextPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantTextSecondary
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@Composable
fun BatchProcessScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    var selectedScale by remember { mutableStateOf(ResizeScale.SCALE_50) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.JPEG) }
    var quality by remember { mutableIntStateOf(85) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantBackground)
            .padding(bottom = 20.dp)
    ) {
        AppHeader(
            title = "Batch Optimizer",
            subtitle = "Email & Upload Shrinker",
            actionIcon = "➕",
            onActionClick = { viewModel.addBatchSampleImages() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = ElegantSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "BATCH RESIZE & FORMAT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = ElegantPrimary
                    )

                    // Target Scale Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            ResizeScale.SCALE_75,
                            ResizeScale.SCALE_50,
                            ResizeScale.SCALE_25,
                            ResizeScale.HD_1080
                        ).forEach { scale ->
                            val selected = selectedScale == scale
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedScale = scale },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) MaterialTheme.colorScheme.primaryContainer else ElegantSurfaceVariant,
                                border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary) else null
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = scale.label.split(" ").first(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (selected) ElegantPrimary else ElegantTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Format Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Output Format",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElegantTextPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ExportFormat.entries.forEach { fmt ->
                                val selected = selectedFormat == fmt
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedFormat = fmt },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) ElegantPrimary else ElegantSurfaceVariant
                                ) {
                                    Text(
                                        text = fmt.extension.uppercase(),
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

                    AdjustmentSlider(
                        label = "Quality",
                        value = quality.toFloat(),
                        range = 30f..100f,
                        valueLabel = "$quality%",
                        onValueChange = { quality = it.toInt() }
                    )
                }
            }

            // Queue List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUEUE (${uiState.batchBitmaps.size} IMAGES)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    ),
                    color = ElegantTextSecondary
                )

                if (uiState.batchBitmaps.isNotEmpty()) {
                    Text(
                        text = "Clear Queue",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFB4AB),
                        modifier = Modifier.clickable { viewModel.clearBatch() }
                    )
                }
            }

            if (uiState.batchBitmaps.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = ElegantSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🛠️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Batch Queue is Empty",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = ElegantTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Load multiple desk photos or craft snapshots to compress them together.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.addBatchSampleImages() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantPrimary,
                                contentColor = ElegantOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Load 3 Demo Photos", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.batchBitmaps) { (name, bmp) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            color = ElegantSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ElegantSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "🖼️", fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        ),
                                        color = ElegantTextPrimary
                                    )
                                    Text(
                                        text = "${bmp.width} × ${bmp.height} px",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ElegantTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Batch Progress / Action
                if (uiState.isBatchProcessing) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { uiState.batchProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = ElegantPrimary,
                            trackColor = ElegantSurfaceVariant
                        )
                        Text(
                            text = "Optimizing ${(uiState.batchProgress * 100).toInt()}%...",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantPrimary
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.runBatchConvert(selectedScale, selectedFormat, quality) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("run_batch_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantPrimary,
                            contentColor = ElegantOnPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Process & Save ${uiState.batchBitmaps.size} Images",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
