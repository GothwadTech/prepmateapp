package com.gothwad.prepmate.data

import kotlinx.coroutines.flow.Flow

class PrepmateRepository(private val prepmateDao: PrepmateDao) {

    val allOfflineDrafts: Flow<List<OfflineDraft>> = prepmateDao.getAllOfflineDrafts()
    val allNotifications: Flow<List<NotificationItem>> = prepmateDao.getAllNotifications()

    suspend fun saveOfflineDraft(content: String, recipient: String = "General") {
        prepmateDao.insertOfflineDraft(OfflineDraft(content = content, recipient = recipient))
    }

    suspend fun deleteOfflineDraft(draft: OfflineDraft) {
        prepmateDao.deleteOfflineDraft(draft)
    }

    suspend fun clearAllDrafts() {
        prepmateDao.clearAllDrafts()
    }

    suspend fun saveNotification(title: String, message: String) {
        prepmateDao.insertNotification(NotificationItem(title = title, message = message))
    }

    suspend fun markNotificationAsRead(id: Int) {
        prepmateDao.markNotificationAsRead(id)
    }

    suspend fun deleteNotification(id: Int) {
        prepmateDao.deleteNotification(id)
    }

    suspend fun clearAllNotifications() {
        prepmateDao.clearAllNotifications()
    }
}
