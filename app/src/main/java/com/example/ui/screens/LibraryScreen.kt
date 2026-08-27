package com.curiositygarage.imagekit.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.R
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.model.SavedImageItem
import com.curiositygarage.imagekit.ui.components.IosCapsuleButton
import com.curiositygarage.imagekit.ui.components.IosIconBadge
import com.curiositygarage.imagekit.ui.components.IosLargeNavigationBar
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
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark
import com.curiositygarage.imagekit.viewmodel.EditorUiState
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
    viewModel: ImageEditorViewModel,
    uiState: EditorUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
            .padding(bottom = 16.dp)
    ) {
        IosLargeNavigationBar(
            title = stringResource(R.string.library_title),
            subtitle = stringResource(R.string.library_subtitle)
        )

        if (uiState.savedImages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = IosCardSurface,
                    border = BorderStroke(0.5.dp, IosSeparatorOpaque)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IosIconBadge(
                            icon = Icons.Default.PhotoLibrary,
                            backgroundColor = IosSystemBlue,
                            size = 52.dp,
                            iconSize = 28.dp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.library_empty_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            ),
                            color = IosLabelPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.library_empty_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = IosLabelSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        IosCapsuleButton(
                            text = stringResource(R.string.library_view_in_editor),
                            onClick = { viewModel.setTab(NavigationTab.STUDIO) },
                            isPrimary = true,
                            testTag = "open_studio_from_empty_library"
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.library_saved_count, uiState.savedImages.size).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp,
                            fontSize = 12.sp
                        ),
                        color = IosLabelTertiary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }

                items(uiState.savedImages) { item ->
                    SavedImageCard(
                        item = item,
                        onShare = {
                            if (item.uri != null) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = item.format.mimeType
                                    putExtra(Intent.EXTRA_STREAM, item.uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedImageCard(
    item: SavedImageItem,
    onShare: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(item.timestamp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = IosCardSurface,
        border = BorderStroke(0.5.dp, IosSeparatorOpaque)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = IosTertiarySystemBackgroundDark
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.format.extension.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = IosSystemBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.filename,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = IosLabelPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.width}×${item.height} px • ${formatBytes(item.sizeBytes)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = IosLabelSecondary
                    )
                    if (item.originalSizeBytes > 0 && item.sizeBytes < item.originalSizeBytes) {
                        val pct = ((item.originalSizeBytes - item.sizeBytes) * 100 / item.originalSizeBytes).toInt()
                        Text(
                            text = " (-$pct%)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = IosSystemGreen
                        )
                    }
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = IosLabelTertiary
                )
            }

            if (item.uri != null) {
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onShare()
                        }
                        .testTag("share_image_button"),
                    shape = CircleShape,
                    color = IosTertiarySystemBackgroundDark
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = IosSystemBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes <= 0 -> "0 KB"
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format(Locale.US, "%.2f MB", bytes.toDouble() / (1024 * 1024))
    }
}
