package com.curiositygarage.imagekit.ui.screens.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.Adjustments
import com.curiositygarage.imagekit.ui.components.AdjustmentSlider
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.theme.IosSystemRed

@Composable
fun AdjustmentSliderPanel(
    adjustments: Adjustments,
    onAdjustmentsChanged: (Adjustments) -> Unit,
    onResetAdjustments: () -> Unit,
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
        IosGroupedCard(header = stringResource(R.string.adjustments_light_header)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                AdjustmentSlider(
                    label = stringResource(R.string.adj_brightness),
                    value = adjustments.brightness,
                    range = -100f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(brightness = it)) },
                    valueLabel = "${adjustments.brightness.toInt()}%"
                )
                AdjustmentSlider(
                    label = stringResource(R.string.adj_contrast),
                    value = adjustments.contrast,
                    range = -100f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(contrast = it)) },
                    valueLabel = "${adjustments.contrast.toInt()}%"
                )
            }
        }

        IosGroupedCard(header = stringResource(R.string.adjustments_color_header)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                AdjustmentSlider(
                    label = stringResource(R.string.adj_saturation),
                    value = adjustments.saturation,
                    range = -100f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(saturation = it)) },
                    valueLabel = "${adjustments.saturation.toInt()}%"
                )
                AdjustmentSlider(
                    label = stringResource(R.string.adj_warmth),
                    value = adjustments.warmth,
                    range = -100f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(warmth = it)) },
                    valueLabel = "${adjustments.warmth.toInt()}%"
                )
            }
        }

        IosGroupedCard(header = stringResource(R.string.adjustments_detail_header)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                AdjustmentSlider(
                    label = stringResource(R.string.adj_vignette),
                    value = adjustments.vignette,
                    range = 0f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(vignette = it)) },
                    valueLabel = "${adjustments.vignette.toInt()}%"
                )
                AdjustmentSlider(
                    label = stringResource(R.string.adj_sharpen),
                    value = adjustments.sharpen,
                    range = 0f..100f,
                    onValueChange = { onAdjustmentsChanged(adjustments.copy(sharpen = it)) },
                    valueLabel = "${adjustments.sharpen.toInt()}%"
                )
            }
        }

        if (!adjustments.isDefault) {
            TextButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onResetAdjustments()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.adj_reset_button),
                    color = IosSystemRed,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
