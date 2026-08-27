package com.curiositygarage.imagekit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.curiositygarage.imagekit.model.NavigationTab
import com.curiositygarage.imagekit.ui.screens.BatchProcessScreen
import com.curiositygarage.imagekit.ui.screens.HomeScreen
import com.curiositygarage.imagekit.ui.screens.LibraryScreen
import com.curiositygarage.imagekit.ui.screens.StudioEditorScreen
import com.curiositygarage.imagekit.ui.theme.IosCardSurface
import com.curiositygarage.imagekit.ui.theme.IosLabelPrimary
import com.curiositygarage.imagekit.ui.theme.IosLabelSecondary
import com.curiositygarage.imagekit.ui.theme.IosLabelTertiary
import com.curiositygarage.imagekit.ui.theme.IosSecondarySystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSeparatorOpaque
import com.curiositygarage.imagekit.ui.theme.IosSystemBackgroundDark
import com.curiositygarage.imagekit.ui.theme.IosSystemBlue
import com.curiositygarage.imagekit.ui.theme.IosTertiarySystemBackgroundDark
import com.curiositygarage.imagekit.viewmodel.ImageEditorViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: ImageEditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto dismiss notification toasts
    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage != null) {
            delay(3000)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(IosSystemBackgroundDark)
            .statusBarsPadding(),
        bottomBar = {
            IosBottomTabBar(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = IosSystemBackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                NavigationTab.HOME -> HomeScreen(viewModel = viewModel, uiState = uiState)
                NavigationTab.STUDIO -> StudioEditorScreen(viewModel = viewModel, uiState = uiState)
                NavigationTab.BATCH -> BatchProcessScreen(viewModel = viewModel, uiState = uiState)
                NavigationTab.LIBRARY -> LibraryScreen(viewModel = viewModel, uiState = uiState)
            }

            // iOS Dynamic Island / Capsule Notification Banner
            AnimatedVisibility(
                visible = uiState.statusMessage != null,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = IosSecondarySystemBackgroundDark,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, IosSeparatorOpaque),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = IosSystemBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = uiState.statusMessage ?: "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                letterSpacing = (-0.1).sp
                            ),
                            color = IosLabelPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IosBottomTabBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xE61C1C1E),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, IosSeparatorOpaque),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Triple(NavigationTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
                    Triple(NavigationTab.STUDIO, Icons.Filled.AutoFixHigh, Icons.Outlined.AutoFixHigh),
                    Triple(NavigationTab.BATCH, Icons.Filled.Layers, Icons.Outlined.Layers),
                    Triple(NavigationTab.LIBRARY, Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary)
                )

                tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                    val isSelected = currentTab == tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tab)
                            }
                            .padding(vertical = 4.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                            contentDescription = tab.label,
                            tint = if (isSelected) IosSystemBlue else IosLabelTertiary,
                            modifier = Modifier.size(23.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                letterSpacing = (-0.1).sp
                            ),
                            color = if (isSelected) IosSystemBlue else IosLabelTertiary
                        )
                    }
                }
            }
        }
    }
}
