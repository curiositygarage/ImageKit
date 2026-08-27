package com.curiositygarage.imagekit.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.model.EditTool
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.ui.components.AppHeader
import com.curiositygarage.imagekit.ui.components.QuickToolCard
import com.curiositygarage.imagekit.ui.theme.ElegantBackground
import com.curiositygarage.imagekit.ui.theme.ElegantOnPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantOutline
import com.curiositygarage.imagekit.ui.theme.ElegantPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantSurface
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceContainerHigh
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceVariant
import com.curiositygarage.imagekit.ui.theme.ElegantTextPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantTextSecondary
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel

@Composable
fun HomeScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
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
            .background(ElegantBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // App Header
        AppHeader(
            title = "ImageKit",
            subtitle = "Pocket Editor",
            actionIcon = "🖼️",
            onActionClick = { viewModel.setTab(NavigationTab.STUDIO) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Import Box (Matches Design HTML)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9.5f)
                    .clip(RoundedCornerShape(28.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("open_gallery_hero_card"),
                shape = RoundedCornerShape(28.dp),
                color = ElegantSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ElegantSurfaceContainerHigh.copy(alpha = 0.5f),
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
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = ElegantPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Open Gallery",
                                    tint = ElegantOnPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Open from Gallery",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = ElegantTextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Supports PNG, JPG, WEBP",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = ElegantTextSecondary
                        )
                    }
                }
            }

            // Quick Desk & Web Demo Sample Presets
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WORKBENCH DEMO SAMPLES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        ),
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "Instant 1-tap test",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ElegantPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SampleChip(
                        icon = "🛂",
                        label = "Passport Photo",
                        onClick = {
                            viewModel.loadSampleImage("passport")
                            viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SampleChip(
                        icon = "📸",
                        label = "8.4MB Raw Photo",
                        onClick = {
                            viewModel.loadSampleImage("raw_photo")
                            viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SampleChip(
                        icon = "🧾",
                        label = "Receipt Clean",
                        onClick = {
                            viewModel.loadSampleImage("receipt")
                            viewModel.setActiveTool(EditTool.FILTERS)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SampleChip(
                        icon = "📦",
                        label = "Shipping Label",
                        onClick = {
                            viewModel.loadSampleImage("shipping")
                            viewModel.setActiveTool(EditTool.REDACT_MARKUP)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Tools Header & 2x2 Grid (Matches Design HTML)
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK TOOLS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp
                        ),
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "Open Studio",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = ElegantPrimary,
                        modifier = Modifier
                            .clickable { viewModel.setTab(NavigationTab.STUDIO) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        icon = "🌐",
                        title = "HTML & Web Fit",
                        subtitle = "Passport & <500KB shrink",
                        onClick = {
                            viewModel.setActiveTool(EditTool.RESIZE_CONVERT)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        icon = "📐",
                        title = "Crop & Orient",
                        subtitle = "Passport 2x2 & A4 doc",
                        onClick = {
                            viewModel.setActiveTool(EditTool.CROP_ROTATE)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        icon = "🧾",
                        title = "Doc & Receipt",
                        subtitle = "Clean paper & ink boost",
                        onClick = {
                            viewModel.setActiveTool(EditTool.FILTERS)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickToolCard(
                        icon = "🛡️",
                        title = "Redact & Privacy",
                        subtitle = "Blackout & pixelate box",
                        onClick = {
                            viewModel.setActiveTool(EditTool.REDACT_MARKUP)
                            viewModel.setTab(NavigationTab.STUDIO)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick feature highlight card for home office
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = ElegantSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF003733)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Privacy Redaction & Offline",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = ElegantTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Blackout serial numbers and addresses locally. 100% on-device processing.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ElegantTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleChip(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = ElegantSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                ),
                color = ElegantTextPrimary,
                maxLines = 1
            )
        }
    }
}
