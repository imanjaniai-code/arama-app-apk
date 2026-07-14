package com.example

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BiometricUnlockScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainShellScreen
import com.example.ui.screens.MainViewModel
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
  private lateinit var mainViewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

    handlePaymentDeepLink(intent)

    setContent {
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

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handlePaymentDeepLink(intent)
  }

  private fun handlePaymentDeepLink(intent: Intent?) {
    val data = intent?.data ?: return
    if (data.scheme == "arama" && data.host == "payment-callback") {
      val refId = data.getQueryParameter("refId") ?: data.getQueryParameter("refid")
      val amountStr = data.getQueryParameter("amount")
      val plan = data.getQueryParameter("plan") ?: "PREMIUM"

      if (!refId.isNullOrEmpty()) {
        mainViewModel.verifyAndUpgradeSubscription(
          refId = refId,
          amount = amountStr?.toLongOrNull() ?: 49000L,
          plan = plan
        )
      } else {
        mainViewModel.setPaymentErrorMessage("پرداخت ناموفق بود یا توسط کاربر لغو شد.")
      }
    }
  }
}

