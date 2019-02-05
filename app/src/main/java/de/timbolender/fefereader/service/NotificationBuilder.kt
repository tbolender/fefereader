package de.timbolender.fefereader.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import de.timbolender.fefereader.R
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Helper to create notification to current database state.
 */
class NotificationBuilder(private val context: Context, private val repository: DataRepository) {
    private suspend fun createNotificationMessage(): String {
        return withContext(Dispatchers.IO) {
            val numNew = repository.newPostsCount.toInt()
            val numUpdated = repository.updatedPostsCount.toInt()
            val newString = context.getResources()
                    .getQuantityString(R.plurals.notification_new_posts, numNew, numNew)
            val updatedString = context.getResources()
                    .getQuantityString(R.plurals.notification_updated_posts, numUpdated, numUpdated)

            when {
                numNew == 0 -> String.format(Locale.ENGLISH, "Es gibt %s für dich.", updatedString)
                numUpdated == 0 -> String.format(Locale.ENGLISH, "Es gibt %s für dich.", newString)
                else -> String.format(Locale.ENGLISH, "Es gibt %s und %s für dich.", newString, updatedString)
            }
        }
    }

    suspend fun isNotificationNecessary(): Boolean
            = withContext(Dispatchers.IO) {repository.newPostsCount > 0 || repository.updatedPostsCount > 0}

    suspend fun createNotification(channelId: String): Notification {
        val message = createNotificationMessage()
        val startIntent = Intent(context, MainActivity::class.java)
        val startPendingIntent = PendingIntent.getActivity(
                context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(context, channelId)
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(message)
            .setContentIntent(startPendingIntent)
            .setOnlyAlertOnce(true)
            .setShowWhen(true)
            .build()
    }
}
