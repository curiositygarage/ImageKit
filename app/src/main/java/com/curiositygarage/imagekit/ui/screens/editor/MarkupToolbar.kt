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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.curiositygarage.imagekit.model.MarkupItem
import com.curiositygarage.imagekit.model.MarkupType
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosSystemRed
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark

@Composable
fun MarkupToolbar(
    activeMarkupType: MarkupType,
    onMarkupTypeSelected: (MarkupType) -> Unit,
    markups: List<MarkupItem>,
    onRemoveMarkup: (String) -> Unit,
    onClearMarkups: () -> Unit,
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
        IosGroupedCard(
            header = stringResource(R.string.markup_tools_header),
            footer = stringResource(R.string.markup_tap_instruction)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MarkupType.entries) { type ->
                    val isSelected = activeMarkupType == type
                    val icon: ImageVector = when (type) {
                        MarkupType.BLACKOUT -> Icons.Default.CropSquare
                        MarkupType.PIXELATE -> Icons.Default.GridOn
                        MarkupType.BLUR_BOX -> Icons.Default.BlurOn
                        MarkupType.HIGHLIGHT -> Icons.Default.Highlight
                        MarkupType.ARROW -> Icons.AutoMirrored.Filled.ArrowForward
                        MarkupType.TEXT -> Icons.Default.TextFields
                    }

                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMarkupTypeSelected(type)
                            }
                            .testTag("markup_tool_${type.name.lowercase()}"),
                        shape = CircleShape,
                        color = if (isSelected) IosSystemBlue else IosTertiarySystemBackgroundDark,
                        border = if (!isSelected) BorderStroke(0.5.dp, IosSeparatorOpaque) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else IosSystemBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    letterSpacing = (-0.1).sp
                                ),
                                color = if (isSelected) Color.White else IosLabelPrimary
                            )
                        }
                    }
                }
            }
        }

        if (markups.isNotEmpty()) {
            IosGroupedCard(header = "Active Annotations (${markups.size})") {
                Column(modifier = Modifier.padding(12.dp)) {
                    markups.forEach { markup ->
                        val icon: ImageVector = when (markup.type) {
                            MarkupType.BLACKOUT -> Icons.Default.CropSquare
                            MarkupType.PIXELATE -> Icons.Default.GridOn
                            MarkupType.BLUR_BOX -> Icons.Default.BlurOn
                            MarkupType.HIGHLIGHT -> Icons.Default.Highlight
                            MarkupType.ARROW -> Icons.AutoMirrored.Filled.ArrowForward
                            MarkupType.TEXT -> Icons.Default.TextFields
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = IosSystemBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (markup.type == MarkupType.TEXT) markup.text else markup.type.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        letterSpacing = (-0.1).sp
                                    ),
                                    color = IosLabelPrimary
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onRemoveMarkup(markup.id)
                                    },
                                shape = CircleShape,
                                color = IosTertiarySystemBackgroundDark
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = IosSystemRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClearMarkups()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(R.string.markup_clear_all),
                            color = IosSystemRed,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}
