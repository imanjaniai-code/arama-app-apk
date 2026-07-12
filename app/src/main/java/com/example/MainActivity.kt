package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BiometricUnlockScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainShellScreen
import com.example.ui.screens.MainViewModel
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val mainViewModel: MainViewModel = viewModel()
      val currentRoute by mainViewModel.currentRoute.collectAsState()
      val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
      val isBiometricUnlocked by mainViewModel.isBiometricUnlocked.collectAsState()

      MyApplicationTheme(darkTheme = isDarkTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
          when (currentRoute) {
            "onboarding" -> OnboardingScreen(mainViewModel)
            "login" -> LoginScreen(mainViewModel)
            else -> {
              if (isBiometricUnlocked) {
                MainShellScreen(mainViewModel)
              } else {
                BiometricUnlockScreen(mainViewModel)
              }
            }
          }
        }
      }
    }
  }
}

