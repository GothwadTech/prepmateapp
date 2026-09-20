package com.gothwad.prepmate.ui

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.gothwad.prepmate.BuildConfig
import java.util.UUID

/**
 * Native bridge exposed to the Prepmate web app as window.PrepmateApp (see app.jsBridgeName
 * in gradle.properties). Every method here is callable from the page's JavaScript.
 */
class PrepmateJavascriptInterface(
    private val context: Context,
    private val viewModel: PrepmateViewModel
) {
    private val tag = "PrepmateJavascriptInterface"
    // Fallback token handed to the page when Firebase is not configured (derived from app.name).
    private val appToken =
        BuildConfig.APP_NAME.lowercase().replace(" ", "_") + "_app_tok_" +
            UUID.randomUUID().toString().substring(0, 8)

    /**
     * Trigger a native Android push/local notification from JavaScript.
     * JavaScript call: window.PrepmateApp.postNotification("Mock Test", "Your result is ready");
     */
    @JavascriptInterface
    fun postNotification(title: String, message: String) {
        Log.d(tag, "postNotification: Title=$title, Message=$message")
        viewModel.triggerLocalNotification(title, message)
    }

    /**
     * Allows website to request a push registration token.
     * JavaScript call: var token = window.PrepmateApp.getPushToken();
     */
    @JavascriptInterface
    fun getPushToken(): String {
        val sharedPrefs = context.getSharedPreferences(BuildConfig.PREFS_NAME, Context.MODE_PRIVATE)
        val cachedToken = sharedPrefs.getString("fcm_token", null)
        Log.d(tag, "getPushToken requested. Cached token available: ${cachedToken != null}")
        return cachedToken ?: appToken
    }

    /**
     * Check if device is connected to internet.
     * JavaScript call: var online = window.PrepmateApp.isDeviceOnline();
     */
    @JavascriptInterface
    fun isDeviceOnline(): Boolean {
        return viewModel.isOnline.value
    }

    /**
     * Save an offline draft from the web app client.
     * JavaScript call: window.PrepmateApp.saveOfflineDraft("Draft text goes here");
     */
    @JavascriptInterface
    fun saveOfflineDraft(content: String) {
        Log.d(tag, "saveOfflineDraft: $content")
        viewModel.saveDraft(content)
    }

    /**
     * Show a simple toast message.
     * JavaScript call: window.PrepmateApp.showToast("Logged in successfully!");
     */
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Notify native Android container of a theme change with dark parameter (boolean).
     * JavaScript call: window.PrepmateApp.setTheme(true);
     */
    @JavascriptInterface
    fun setTheme(isDark: Boolean) {
        Log.d(tag, "setTheme(Boolean) received: isDark=$isDark")
        viewModel.setDarkThemeOverride(isDark)
    }

    /**
     * Notify native Android container of a theme change with theme name (string).
     * JavaScript call: window.PrepmateApp.setTheme("dark"); or window.PrepmateApp.setTheme("light");
     */
    @JavascriptInterface
    fun setTheme(theme: String) {
        val isDark = when (theme.lowercase().trim()) {
            "dark" -> true
            "light" -> false
            else -> null
        }
        Log.d(tag, "setTheme(String) received: theme=$theme -> mapped isDark=$isDark")
        viewModel.setDarkThemeOverride(isDark)
    }
}
