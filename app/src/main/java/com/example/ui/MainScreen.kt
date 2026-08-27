package com.curiositygarage.imagekit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.curiositygarage.imagekit.ui.theme.ElegantBackground
import com.curiositygarage.imagekit.ui.theme.ElegantBottomNav
import com.curiositygarage.imagekit.ui.theme.ElegantOnPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantOutline
import com.curiositygarage.imagekit.ui.theme.ElegantPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantSurfaceVariant
import com.curiositygarage.imagekit.ui.theme.ElegantTextPrimary
import com.curiositygarage.imagekit.ui.theme.ElegantTextSecondary
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
            .background(ElegantBackground)
            .statusBarsPadding(),
        bottomBar = {
            ElegantBottomNavigationBar(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = ElegantBackground
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

            // Floating Toast Notification
            AnimatedVisibility(
                visible = uiState.statusMessage != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElegantSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimary.copy(alpha = 0.6f)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✨", fontSize = 16.sp)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = uiState.statusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = ElegantTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ElegantBottomNavigationBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ElegantOutline,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ),
        color = ElegantBottomNav
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ElegantPrimary,
                            modifier = Modifier
                                .height(30.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = tab.icon,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.height(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.icon,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) ElegantPrimary else ElegantTextSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
