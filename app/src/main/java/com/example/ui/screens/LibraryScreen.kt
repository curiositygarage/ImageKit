package com.curiositygarage.imagekit.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.model.SavedImageItem
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
            .background(ElegantBackground)
            .padding(bottom = 16.dp)
    ) {
        AppHeader(
            title = "Exports Library",
            subtitle = "Saved Creations",
            actionIcon = "🖼️",
            onActionClick = { viewModel.setTab(NavigationTab.STUDIO) }
        )

        if (uiState.savedImages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = ElegantSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "📁", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Exports Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElegantTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Images saved from the Studio or Batch Optimizer will appear here with full metadata.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.setTab(NavigationTab.STUDIO) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantPrimary,
                                contentColor = ElegantOnPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("open_studio_from_empty_library")
                        ) {
                            Text("Open Studio Editor", fontWeight = FontWeight.Bold)
                        }
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
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(item.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ElegantSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantOutline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantSurfaceVariant,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (item.format.extension) {
                            "png" -> "PNG"
                            "webp" -> "WEBP"
                            else -> "JPG"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElegantPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.filename,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    ),
                    color = ElegantTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.width}×${item.height} px",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = formatBytes(item.sizeBytes),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ElegantPrimary
                    )
                    if (item.originalSizeBytes != null && item.originalSizeBytes > item.sizeBytes) {
                        val pct = ((item.originalSizeBytes - item.sizeBytes).toFloat() / item.originalSizeBytes * 100).toInt()
                        if (pct > 0) {
                            Text(
                                text = "(-$pct%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ElegantPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ElegantTextSecondary.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = onShare,
                modifier = Modifier.testTag("share_image_button_${item.id.take(4)}")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = ElegantPrimary
                )
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.2f MB", bytes.toDouble() / (1024 * 1024))
    }
}
