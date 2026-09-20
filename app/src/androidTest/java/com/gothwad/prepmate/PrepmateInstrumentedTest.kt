package com.gothwad.prepmate

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4::class)
class PrepmateInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the Prepmate app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.gothwad.prepmate", appContext.packageName)
  }
}
