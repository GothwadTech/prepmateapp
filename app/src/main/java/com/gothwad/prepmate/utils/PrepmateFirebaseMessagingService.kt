package com.gothwad.prepmate.utils

import android.util.Log
import com.gothwad.prepmate.BuildConfig
import com.gothwad.prepmate.data.PrepmateDatabase
import com.gothwad.prepmate.data.PrepmateRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Receives Firebase Cloud Messaging pushes for Prepmate and surfaces them as
 * native notifications (plus a local Room log the app can show later).
 */
class PrepmateFirebaseMessagingService : FirebaseMessagingService() {

    private val tag = "PrepmateFCMService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Called when a new FCM token is generated or refreshed.
     * The Prepmate web app reads it through window.PrepmateApp.getPushToken() and
     * registers it with the Prepmate backend.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "Refreshed FCM Token: $token")

        // Cache the token locally so the JS bridge can hand it to the web app
        val sharedPrefs = getSharedPreferences(BuildConfig.PREFS_NAME, MODE_PRIVATE)
        sharedPrefs.edit().putString("fcm_token", token).apply()
    }

    /**
     * Called when a message is received while the app is in the background or foreground.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(tag, "From: ${remoteMessage.from}")

        // 1. Extract title and body from the notification payload or the data payload
        var title = remoteMessage.notification?.title
        var body = remoteMessage.notification?.body

        // Data-only pushes (sent by the Prepmate backend for full control over rendering)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tag, "Message data payload: ${remoteMessage.data}")
            if (title.isNullOrEmpty()) {
                title = remoteMessage.data["title"]
            }
            if (body.isNullOrEmpty()) {
                body = remoteMessage.data["message"] ?: remoteMessage.data["body"]
            }
        }

        val finalTitle = title ?: BuildConfig.APP_NAME
        val finalBody = body ?: "You have a new update from ${BuildConfig.APP_NAME}."

        Log.d(tag, "Displaying notification: Title=$finalTitle, Body=$finalBody")

        // 2. Persist the notification in the local Room database (in-app notification history)
        saveNotificationToLocalDb(finalTitle, finalBody)

        // 3. Show the native system notification
        PrepmateNotificationHelper.showNotification(applicationContext, finalTitle, finalBody)
    }

    private fun saveNotificationToLocalDb(title: String, message: String) {
        serviceScope.launch {
            try {
                val db = PrepmateDatabase.getDatabase(applicationContext)
                val repository = PrepmateRepository(db.prepmateDao())
                repository.saveNotification(title, message)
            } catch (e: Exception) {
                Log.e(tag, "Failed to persist notification in Room DB", e)
            }
        }
    }
}
