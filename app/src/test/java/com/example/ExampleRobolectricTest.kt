package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.arama.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

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
}

