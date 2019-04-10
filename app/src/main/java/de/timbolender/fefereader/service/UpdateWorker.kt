package de.timbolender.fefereader.service

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import de.timbolender.fefereader.db.DataRepository
import de.timbolender.fefereader.network.Updater
import java.io.IOException
import java.text.ParseException

class UpdateWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    companion object {
        private var TAG: String = UpdateWorker::class.simpleName!!
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
