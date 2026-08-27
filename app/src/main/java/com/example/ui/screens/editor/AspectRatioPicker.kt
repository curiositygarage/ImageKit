package com.curiositygarage.imagekit.ui.screens.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.curiositygarage.imagekit.model.AspectRatioPreset
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark

@Composable
fun AspectRatioPicker(
    selectedRatio: AspectRatioPreset,
    onRatioSelected: (AspectRatioPreset) -> Unit,
    onRotate: (Float) -> Unit,
    onFlip: (Boolean, Boolean) -> Unit,
    onApplyCrop: () -> Unit,
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
        IosGroupedCard(header = stringResource(R.string.aspect_ratio_header)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AspectRatioPreset.entries) { preset ->
                    val isSelected = selectedRatio == preset
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRatioSelected(preset)
                            }
                            .testTag("ratio_${preset.name.lowercase()}"),
                        shape = CircleShape,
                        color = if (isSelected) IosSystemBlue else IosTertiarySystemBackgroundDark,
                        border = if (!isSelected) BorderStroke(0.5.dp, IosSeparatorOpaque) else null
                    ) {
                        Text(
                            text = preset.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = if (isSelected) Color.White else IosLabelPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        IosGroupedCard(header = stringResource(R.string.orientation_header)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TransformIconButton(
                    icon = Icons.Default.Rotate90DegreesCcw,
                    label = "-90°",
                    onClick = { onRotate(-90f) },
                    testTag = "rotate_left_btn"
                )
                TransformIconButton(
                    icon = Icons.Default.Rotate90DegreesCw,
                    label = "+90°",
                    onClick = { onRotate(90f) },
                    testTag = "rotate_right_btn"
                )
                TransformIconButton(
                    icon = Icons.Default.Flip,
                    label = stringResource(R.string.flip_horizontal),
                    onClick = { onFlip(true, false) },
                    testTag = "flip_h_btn"
                )
                TransformIconButton(
                    icon = Icons.Default.SwapVert,
                    label = stringResource(R.string.flip_vertical),
                    onClick = { onFlip(false, true) },
                    testTag = "flip_v_btn"
                )
            }
        }

        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onApplyCrop()
            },
            colors = ButtonDefaults.buttonColors(containerColor = IosSystemBlue),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("apply_crop_button")
        ) {
            Icon(imageVector = Icons.Default.Crop, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.apply_crop_button),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.1).sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun TransformIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .testTag(testTag),
        shape = CircleShape,
        color = IosTertiarySystemBackgroundDark,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IosSystemBlue,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = (-0.1).sp
                ),
                color = IosLabelPrimary
            )
        }
    }
}
