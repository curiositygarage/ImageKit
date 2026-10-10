package com.curiositygarage.imagekit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.ui.theme.IosCardSurface
import com.curiositygarage.imagekit.ui.theme.IosFillQuaternary
import com.curiositygarage.imagekit.ui.theme.IosFillTertiary
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparator
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark

/**
 * Authentic Apple iOS Icon Badge (30-32dp rounded square container with solid saturated color & white SF-style icon)
 */
@Composable
fun IosIconBadge(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    iconSize: Dp = 18.dp
) {
    Surface(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * iOS 17 Inset Grouped Navigation Bar with Large Title and SF Pro typography hierarchy
 */
@Composable
fun IosLargeNavigationBar(
    title: String,
    subtitle: String? = null,
    actionButton: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (subtitle != null) {
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = IosSystemBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp
                    ),
                    color = IosLabelPrimary
                )
            }

            if (actionButton != null) {
                actionButton()
            }
        }
    }
}

@Composable
fun AppHeader(
    title: String = "ImageKit",
    subtitle: String = "Pocket Editor",
    onActionClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    IosLargeNavigationBar(
        title = title,
        subtitle = subtitle,
        actionButton = if (onActionClick != null && actionIcon != null) {
            {
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onActionClick() }
                        .testTag("header_action_button"),
                    shape = CircleShape,
                    color = IosTertiarySystemBackgroundDark,
                    border = BorderStroke(0.5.dp, IosSeparatorOpaque)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = null,
                            tint = IosSystemBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        } else null,
        modifier = modifier
    )
}

/**
 * iOS Grid Quick Tool Card with Apple SF icon badge and crisp typography
 */
@Composable
fun QuickToolCard(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .testTag("tool_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        color = IosCardSurface,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IosIconBadge(
                icon = icon,
                backgroundColor = iconBgColor,
                size = 38.dp,
                iconSize = 22.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = IosLabelPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        letterSpacing = (-0.1).sp
                    ),
                    color = IosLabelSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Apple iOS Pill / Capsule Button
 */
@Composable
fun IosCapsuleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "capsule_button"
) {
    val haptics = LocalHapticFeedback.current
    val bgColor = if (isPrimary) IosSystemBlue else IosTertiarySystemBackgroundDark
    val contentColor = if (isPrimary) Color.White else IosLabelPrimary

    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .testTag(testTag),
        shape = CircleShape,
        color = bgColor,
        border = if (!isPrimary) BorderStroke(0.5.dp, IosSeparatorOpaque) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = (-0.1).sp
                ),
                color = contentColor
            )
        }
    }
}

/**
 * Apple iOS Inset Grouped Table View Card container (12dp radius, 0.5dp hairline border)
 */
@Composable
fun IosGroupedCard(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (header != null) {
            Text(
                text = header.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                    fontSize = 12.sp
                ),
                color = IosLabelTertiary,
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = IosCardSurface,
            border = BorderStroke(0.5.dp, IosSeparatorOpaque)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }

        if (footer != null) {
            Text(
                text = footer,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    letterSpacing = (-0.1).sp
                ),
                color = IosLabelTertiary,
                modifier = Modifier.padding(start = 16.dp, top = 6.dp)
            )
        }
    }
}

/**
 * Apple iOS Inset Grouped List Row with SF badge icon, typography, and aligned divider
 */
@Composable
fun IosListRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconBgColor: Color = IosSystemBlue,
    trailingText: String? = null,
    showChevron: Boolean = false,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    } else {
        modifier.fillMaxWidth()
    }

    Column(modifier = rowModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                IosIconBadge(
                    icon = icon,
                    backgroundColor = iconBgColor,
                    size = 30.dp,
                    iconSize = 18.dp,
                    modifier = Modifier.padding(end = 14.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = IosLabelPrimary
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            letterSpacing = (-0.1).sp
                        ),
                        color = IosLabelSecondary
                    )
                }
            }

            if (trailingText != null) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        letterSpacing = (-0.1).sp
                    ),
                    color = IosLabelSecondary,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = IosLabelTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = IosSeparator,
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = if (icon != null) 60.dp else 16.dp)
            )
        }
    }
}

/**
 * iOS Slider with precise value badge
 */
@Composable
fun AdjustmentSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = (-0.1).sp
                ),
                color = IosLabelPrimary
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = IosTertiarySystemBackgroundDark,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
            ) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = IosSystemBlue,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = IosSystemBlue,
                inactiveTrackColor = IosFillTertiary
            ),
            modifier = Modifier.testTag("slider_${label.lowercase().replace(" ", "_")}")
        )
    }
}

/**
 * Apple iOS Segmented Control (Sliding selection container)
 */
@Composable
fun <T> IosSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = IosTertiarySystemBackgroundDark,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF636366) else Color.Transparent,
                    label = "segmented_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else IosLabelSecondary,
                    label = "segmented_text"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onItemSelected(item)
                        },
                    shape = RoundedCornerShape(6.dp),
                    color = bgColor
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = labelProvider(item),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = textColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
