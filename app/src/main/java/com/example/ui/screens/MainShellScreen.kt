package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrisisRed
import com.example.ui.theme.CrisisRedBg
import com.example.ui.theme.SageDeep
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SageTintBg

@Composable
fun MainShellScreen(
    viewModel: MainViewModel
) {
    val currentRoute by viewModel.currentRoute.collectAsState()
    val isEmergencyVisible by viewModel.isEmergencyVisible.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_shell_root"),
        topBar = {
            // Unified Top Bar (constant design across all authenticated screens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right side: Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SagePrimary, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "آ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "آراﻣﺎ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDeep,
                                fontSize = 20.sp,
                                lineHeight = 20.sp
                            )
                        )
                        Text(
                            text = "ARAMA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SageDeep.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp,
                                lineHeight = 9.sp
                            )
                        )
                    }
                }

                // Left side: Emergency Help Button
                androidx.compose.material3.IconButton(
                    onClick = { viewModel.setEmergencyVisible(true) },
                    modifier = Modifier.testTag("emergency_button_shell")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationImportant,
                        contentDescription = "کمک اضطراری",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        bottomBar = {
            // Unified bottom navigation bar with active pills - simplified to 3 tabs
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("app_navigation_bar"),
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = currentRoute == "chat",
                    onClick = { viewModel.navigate("chat") },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "گفتگو"
                        )
                    },
                    label = { Text("گفتگو") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SageDeep,
                        selectedTextColor = SageDeep,
                        indicatorColor = SageTintBg
                    ),
                    modifier = Modifier.testTag("nav_item_chat")
                )

                NavigationBarItem(
                    selected = currentRoute == "dashboard" || currentRoute == "mood",
                    onClick = { viewModel.navigate("dashboard") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.InsertChart,
                            contentDescription = "نمودار احساسات"
                        )
                    },
                    label = { Text("نمودار احساسات") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SageDeep,
                        selectedTextColor = SageDeep,
                        indicatorColor = SageTintBg
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                NavigationBarItem(
                    selected = currentRoute == "settings",
                    onClick = { viewModel.navigate("settings") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "تنظیمات"
                        )
                    },
                    label = { Text("تنظیمات") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SageDeep,
                        selectedTextColor = SageDeep,
                        indicatorColor = SageTintBg
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "chat" -> ChatScreen(viewModel)
                "mood" -> MoodScreen(viewModel)
                "dashboard" -> DashboardScreen(viewModel)
                "settings" -> SettingsScreen(viewModel)
                else -> ChatScreen(viewModel) // fallback
            }
        }
    }

    // Overlay Emergency helplines dialog if visible
    if (isEmergencyVisible) {
        EmergencyDialog(
            onDismiss = { viewModel.setEmergencyVisible(false) }
        )
    }
}
