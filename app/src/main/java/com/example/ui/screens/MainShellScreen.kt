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
import androidx.compose.material.icons.filled.Chat
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
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen

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
                            .background(EmeraldPrimary, shape = RoundedCornerShape(12.dp)),
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
                            text = "آراما",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                fontSize = 20.sp,
                                lineHeight = 20.sp
                            )
                        )
                        Text(
                            text = "ARAMA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldDark.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.5.sp,
                                lineHeight = 9.sp
                            )
                        )
                    }
                }

                // Placeholder or spacer to maintain clean topBar spacing
                Spacer(modifier = Modifier.width(1.dp))
            }
        },
        bottomBar = {
            // Unified bottom navigation bar with active pills
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
                            imageVector = Icons.Default.Chat,
                            contentDescription = "گفتگو"
                        )
                    },
                    label = { Text("گفتگو") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldDark,
                        indicatorColor = MintGreen
                    ),
                    modifier = Modifier.testTag("nav_item_chat")
                )

                NavigationBarItem(
                    selected = currentRoute == "mood",
                    onClick = { viewModel.navigate("mood") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Mood,
                            contentDescription = "ثبت احوال"
                        )
                    },
                    label = { Text("احوال روحی") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldDark,
                        indicatorColor = MintGreen
                    ),
                    modifier = Modifier.testTag("nav_item_mood")
                )

                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = { viewModel.navigate("dashboard") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.InsertChart,
                            contentDescription = "نمودارها"
                        )
                    },
                    label = { Text("نمودارها") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldDark,
                        indicatorColor = MintGreen
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
                        selectedIconColor = EmeraldDark,
                        selectedTextColor = EmeraldDark,
                        indicatorColor = MintGreen
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
