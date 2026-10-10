package com.curiositygarage.imagekit.ui.screens.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterBAndW
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.model.FilterPreset
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark

@Composable
fun FilterStrip(
    selectedFilter: FilterPreset,
    onFilterSelected: (FilterPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(FilterPreset.entries) { filter ->
                val isSelected = selectedFilter == filter
                val icon: ImageVector = when (filter) {
                    FilterPreset.ORIGINAL -> Icons.Default.Palette
                    FilterPreset.DOC_CLEAN -> Icons.Default.Description
                    FilterPreset.WHITEBOARD -> Icons.Default.BrightnessMedium
                    FilterPreset.MAGIC_COLOR -> Icons.Default.ColorLens
                    FilterPreset.AUTO_ENHANCE -> Icons.Default.AutoFixHigh
                    FilterPreset.BW_CONTRAST -> Icons.Default.FilterBAndW
                    FilterPreset.VIVID -> Icons.Default.AutoAwesome
                    FilterPreset.VINTAGE_WARM -> Icons.Default.WbSunny
                    FilterPreset.COOL_SLATE -> Icons.Default.FormatPaint
                    FilterPreset.INVERT -> Icons.Default.InvertColors
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onFilterSelected(filter)
                        }
                        .testTag("filter_preset_${filter.name.lowercase()}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) IosTertiarySystemBackgroundDark else IosSecondarySystemBackgroundDark,
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 0.5.dp,
                        if (isSelected) IosSystemBlue else IosSeparatorOpaque
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) IosSystemBlue else IosTertiarySystemBackgroundDark,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else IosSystemBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = if (isSelected) IosSystemBlue else IosLabelPrimary
                        )
                    }
                }
            }
        }
    }
}
