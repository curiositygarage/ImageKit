package com.curiositygarage.imagekit.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.ui.components.AppHeader
import com.curiositygarage.imagekit.ui.components.IosGroupedCard
import com.curiositygarage.imagekit.ui.components.IosListRow
import com.curiositygarage.imagekit.ui.components.QuickToolCard
import com.curiositygarage.imagekit.ui.theme.IosCardSurface
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosSystemGreen
import com.curiositygarage.imagekit.ui.theme.IosSystemIndigo
import com.curiositygarage.imagekit.ui.theme.IosSystemOrange
import com.curiositygarage.imagekit.ui.theme.IosSystemPurple
import com.curiositygarage.imagekit.ui.theme.IosSystemRed
import com.curiositygarage.imagekit.ui.theme.IosSystemTeal
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@Composable
fun HomeScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.loadImageFromUri(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // iOS Large Navigation Header
        AppHeader(
            title = stringResource(R.string.app_name),
            subtitle = stringResource(R.string.app_subtitle),
            actionIcon = Icons.Outlined.AutoFixHigh,
            onActionClick = { viewModel.setTab(NavigationTab.STUDIO) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Photo Import Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("open_gallery_hero_card"),
                shape = RoundedCornerShape(16.dp),
                color = IosCardSurface,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, IosSeparatorOpaque)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    IosSystemBlue.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape),
                            shape = CircleShape,
                            color = IosSystemBlue
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Open Gallery",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.home_hero_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = IosLabelPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = stringResource(R.string.home_hero_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = IosLabelSecondary
                        )
                    }
                }
            }

            // Workbench Demo Samples
            IosGroupedCard(
                header = stringResource(R.string.home_section_samples),
                footer = stringResource(R.string.home_samples_subtitle)
            ) {
                IosListRow(
                    title = "Passport & Visa Portrait",
                    subtitle = "600×600 px • Target < 240 KB spec",
                    icon = Icons.Default.Badge,
                    iconBgColor = IosSystemBlue,
                    showChevron = true,
                    onClick = {
                        viewModel.loadSampleImage("passport")
                        viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                        viewModel.setTab(NavigationTab.STUDIO)
                    }
                )
                IosListRow(
                    title = "Hardware Receipt & Invoice",
                    subtitle = "Dynamic shadow clean & dark ink thresholding",
                    icon = Icons.Default.Receipt,
                    iconBgColor = IosSystemOrange,
                    showChevron = true,
                    onClick = {
                        viewModel.loadSampleImage("receipt")
                        viewModel.setActiveTool(EditTool.FILTERS)
                        viewModel.setTab(NavigationTab.STUDIO)
                    }
                )
                IosListRow(
                    title = "8.4MB Raw Camera Photo",
                    subtitle = "Test large memory downscaling & JPEG optimizer",
                    icon = Icons.Default.PhotoCamera,
                    iconBgColor = IosSystemGreen,
                    showChevron = true,
                    onClick = {
                        viewModel.loadSampleImage("raw_photo")
                        viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                        viewModel.setTab(NavigationTab.STUDIO)
                    }
                )
                IosListRow(
                    title = "Shipping Label & Address",
                    subtitle = "Test blackout privacy and redactions",
                    icon = Icons.Default.Security,
                    iconBgColor = IosSystemIndigo,
                    showChevron = true,
                    showDivider = false,
                    onClick = {
                        viewModel.loadSampleImage("shipping")
                        viewModel.setActiveTool(EditTool.REDACT_MARKUP)
                        viewModel.setTab(NavigationTab.STUDIO)
                    }
                )
            }

            // Quick Studio Tools (2x2 Grid)
            Column {
                Text(
                    text = stringResource(R.string.home_section_quick_tools).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.6.sp,
                        fontSize = 12.sp
                    ),
                    color = IosLabelTertiary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickToolCard(
                        icon = Icons.Default.Compress,
                        iconBgColor = IosSystemBlue,
                        title = stringResource(R.string.tool_web_optimizer),
                        subtitle = "< 500 KB Form Fit",
                        onClick = {
                            viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        icon = Icons.Default.Crop,
                        iconBgColor = IosSystemPurple,
                        title = stringResource(R.string.tool_crop_rotate),
                        subtitle = "Passport 2×2 & A4",
                        onClick = {
                            viewModel.setActiveTool(EditTool.CROP_ROTATE)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickToolCard(
                        icon = Icons.Default.AutoAwesome,
                        iconBgColor = IosSystemOrange,
                        title = stringResource(R.string.tool_filters),
                        subtitle = "Clean Paper & Ink",
                        onClick = {
                            viewModel.setActiveTool(EditTool.FILTERS)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        icon = Icons.Default.Brush,
                        iconBgColor = IosSystemRed,
                        title = stringResource(R.string.tool_redact_markup),
                        subtitle = "Blackout & Annotate",
                        onClick = {
                            viewModel.setActiveTool(EditTool.REDACT_MARKUP)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Pro Capabilities Inset Card
            IosGroupedCard(header = stringResource(R.string.home_features_title)) {
                IosListRow(
                    title = stringResource(R.string.home_feature_offline_title),
                    subtitle = stringResource(R.string.home_feature_offline_desc),
                    icon = Icons.Default.Lock,
                    iconBgColor = IosSystemGreen,
                    showDivider = true
                )
                IosListRow(
                    title = stringResource(R.string.home_feature_web_title),
                    subtitle = stringResource(R.string.home_feature_web_desc),
                    icon = Icons.Default.Bolt,
                    iconBgColor = IosSystemBlue,
                    showDivider = true
                )
                IosListRow(
                    title = stringResource(R.string.home_feature_passport_title),
                    subtitle = stringResource(R.string.home_feature_passport_desc),
                    icon = Icons.Default.CheckCircle,
                    iconBgColor = IosSystemPurple,
                    showDivider = false
                )
            }
        }
    }
}
