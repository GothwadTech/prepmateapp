package com.gothwad.prepmate

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.gothwad.prepmate.ui.theme.PrepmateTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Roborazzi screenshot smoke test for the Prepmate Compose theme.
 * Record / verify with: ./gradlew recordRoborazziDebug | verifyRoborazziDebug
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class PrepmateThemeScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun prepmate_theme_screenshot() {
    composeTestRule.setContent { PrepmateTheme { Text("Prepmate") } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/prepmate_theme.png")
  }
}
