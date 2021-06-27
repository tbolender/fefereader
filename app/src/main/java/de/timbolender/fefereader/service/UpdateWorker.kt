package de.timbolender.fefereader.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import de.timbolender.fefereader.BuildConfig
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
class UpdateWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    companion object {
        private var TAG: String = UpdateWorker::class.simpleName!!

        const val BROADCAST_UPDATE_FINISHED = "de.timbolender.fefereader.service.action.UPDATE_FINISHED"
        const val EXTRA_UPDATE_SUCCESS = "update_success"
        const val BROADCAST_PRIORITY_UI = 10
        const val BROADCAST_PRIORITY_SERVICE = 0

        val MANUAL_UPDATE_WORKER = "update-manual"
        val AUTOMATIC_UPDATE_WORKER = "update-automatic"

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
            if(preferenceHelper.isUpdatesEnabled) {
                Log.d(TAG, "Enabling automatic updates")
                val interval = preferenceHelper.updateInterval
                val request = PeriodicWorkRequestBuilder<UpdateWorker>(interval, TimeUnit.MILLISECONDS)
                        .build()
                WorkManager.getInstance(context)
                        .enqueueUniquePeriodicWork(AUTOMATIC_UPDATE_WORKER, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
            else {
                Log.d(TAG, "Disabling automatic updates")
                WorkManager.getInstance(context)
                        .cancelUniqueWork(AUTOMATIC_UPDATE_WORKER)
            }
        }
    }

    val repository: DataRepository = DataRepository(context.applicationContext)

    override fun doWork(): Result {
        try {
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
        }
        return Result.failure()
    }

    private fun sendBroadcastIntent(success: Boolean) {
        val finishedIntent = Intent(BROADCAST_UPDATE_FINISHED)
        finishedIntent.putExtra(EXTRA_UPDATE_SUCCESS, success)
        finishedIntent.setPackage(BuildConfig.APPLICATION_ID);
        applicationContext.sendOrderedBroadcast(finishedIntent, null)
    }
}
