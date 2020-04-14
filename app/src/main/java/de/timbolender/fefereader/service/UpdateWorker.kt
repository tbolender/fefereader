package de.timbolender.fefereader.service

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.*
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.network.Updater
import de.timbolender.fefereader.util.PreferenceHelper
import java.io.IOException
import java.text.ParseException
import java.util.concurrent.TimeUnit

class UpdateWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    companion object {
        private var TAG: String = UpdateWorker::class.simpleName!!

        val MANUAL_UPDATE_WORKER = "update-manual"
        val AUTOMATIC_UPDATE_WORKER = "update-automatic"

        /**
         * Trigger update in background service.
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
            if(!preferenceHelper.isUpdatesEnabled) {
                WorkManager.getInstance(context)
                        .cancelUniqueWork(AUTOMATIC_UPDATE_WORKER)
            }
            else {
                val interval = preferenceHelper.updateInterval
                val automaticConstraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                val request = PeriodicWorkRequestBuilder<UpdateWorker>(interval, TimeUnit.MILLISECONDS)
                        .setConstraints(automaticConstraints)
                        .build()
                WorkManager.getInstance(context)
                        .enqueueUniquePeriodicWork(AUTOMATIC_UPDATE_WORKER, ExistingPeriodicWorkPolicy.REPLACE, request)
            }
        }
    }

    val repository: DataRepository = DataRepository(context.applicationContext as Application)

    override fun doWork(): Result {
        try {
            Updater(repository).update()
            Log.d(TAG, "Update finished")
            return Result.success()
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.retry()
        }
        return Result.failure()
    }
}
