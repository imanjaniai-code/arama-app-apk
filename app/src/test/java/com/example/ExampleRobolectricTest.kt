package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.arama.app.R
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("آراما", appName)
  }

  @Test
  fun `resolve font resource`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val resId = R.font.yekanbakh_regular
    val stream = context.resources.openRawResource(resId)
    assertNotNull(stream)
    stream.close()
  }

  @Test
  fun `diagnose crash during MainViewModel and LoginScreen init`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val mainViewModel = MainViewModel(application)
    assertNotNull(mainViewModel)
    
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = false) {
        LoginScreen(mainViewModel)
      }
    }
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify sendOtpCode flow completes without crashing`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val mainViewModel = MainViewModel(application)
    mainViewModel.setLoginPhone("09123456789")
    mainViewModel.sendOtpCode()
  }
}

