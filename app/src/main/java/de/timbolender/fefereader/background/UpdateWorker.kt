package de.timbolender.fefereader.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import de.timbolender.fefereader.BuildConfig
import de.timbolender.fefereader.R
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.network.Updater
import de.timbolender.fefereader.util.PreferenceHelper
import java.io.IOException
import java.text.ParseException
import java.util.concurrent.TimeUnit

/**
 * Worker fetching the latest post from Fefe and store it in the database.
 * Triggers a retry on connectivity issues. Issues a broadcast intent on finish.
 */
class UpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        private var TAG: String = UpdateWorker::class.simpleName!!

        const val BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED"
        const val EXTRA_UPDATE_SUCCESS = "update_success"
        const val BROADCAST_PRIORITY_UI = 10
        const val BROADCAST_PRIORITY_SERVICE = 0

        const val MANUAL_UPDATE_WORKER = "update-manual"
        const val AUTOMATIC_UPDATE_WORKER = "update-automatic"

        const val NOTIFICATION_CHANNEL_ID = "update-channel"
        const val NOTIFICATION_ID = 37

        /**
         * Trigger one-time update in background service.
         * @param context Context to use.
         */
        fun startManualUpdate(context: Context) {
            val request = OneTimeWorkRequestBuilder<UpdateWorker>()
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(MANUAL_UPDATE_WORKER, ExistingWorkPolicy.KEEP, request)
        }

        /**
         * Start automatic updates in background service.
         * @param context Context to use.
         */
        fun configureAutomaticUpdates(context: Context, preferenceHelper: PreferenceHelper) {
            if (preferenceHelper.isUpdatesEnabled) {
                Log.d(TAG, "Enabling automatic updates")
                val interval = preferenceHelper.updateInterval
                val request = PeriodicWorkRequestBuilder<UpdateWorker>(interval, TimeUnit.MILLISECONDS)
                    .build()
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(AUTOMATIC_UPDATE_WORKER, ExistingPeriodicWorkPolicy.REPLACE, request)
            } else {
                Log.d(TAG, "Disabling automatic updates")
                WorkManager.getInstance(context)
                    .cancelUniqueWork(AUTOMATIC_UPDATE_WORKER)
            }
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(NotificationCreator.TAG, "Create update notification channel")
                val name: String = context.getString(R.string.update_notification_channel_name)
                val descriptionText: String = context.getString(R.string.update_notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_MIN
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    val repository: DataRepository = DataRepository(context.applicationContext)

    override fun doWork(): Result {
        try {
            createNotification()

            Updater(repository).update()
            Log.d(TAG, "Update finished")

            sendBroadcastIntent(true)
            return Result.success()
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.retry()
        } finally {
            sendBroadcastIntent(false)
            hideNotification()
        }
        return Result.failure()
    }

    private fun createNotification() {
        createNotificationChannel(applicationContext)

        Log.d(NotificationCreator.TAG, "Showing notification")
        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.update_notification_title))
            .setContentText(applicationContext.getString(R.string.update_notification_text))

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun hideNotification() {
        NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_ID)
    }

    private fun sendBroadcastIntent(success: Boolean) {
        val finishedIntent = Intent(BROADCAST_UPDATE_FINISHED)
        finishedIntent.putExtra(EXTRA_UPDATE_SUCCESS, success)
        finishedIntent.setPackage(BuildConfig.APPLICATION_ID)
        applicationContext.sendOrderedBroadcast(finishedIntent, null)
    }
}
