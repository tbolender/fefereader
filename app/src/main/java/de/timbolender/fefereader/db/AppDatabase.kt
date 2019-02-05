package de.timbolender.fefereader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.timbolender.fefereader.db.migration.ManualToRoomMigration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(version = 1, entities = [Post::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildDatabase(context)
                }
            }

            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fefe-news")
                .addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Migrate from old manual database version
                        CoroutineScope(Dispatchers.IO).launch {
                            val postDao = getInstance(context).postDao()
                            val initialMigration = ManualToRoomMigration(context, postDao)
                            if(initialMigration.isMigrationNecessary) {
                                initialMigration.migrate()
                            }
                        }
                    }
                })
                .build()
        }
    }

    abstract fun postDao(): PostDao
}
