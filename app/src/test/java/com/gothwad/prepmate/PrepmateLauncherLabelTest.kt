package com.gothwad.prepmate

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrepmateLauncherLabelTest {

  @Test
  fun `launcher label matches the Prepmate configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    // app_name is generated from app.name in gradle.properties, so assert against the
    // configured value: this fails if the config stops reaching the packaged resources.
    assertEquals(BuildConfig.APP_NAME, appName)
    assertEquals("Prepmate", appName)
  }
}
