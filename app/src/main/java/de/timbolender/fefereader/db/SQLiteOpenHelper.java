package de.timbolender.fefereader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.timbolender.fefereader.db.SQLiteFefesBlogContract.*;

/**
 * Helper for initialising database.
 */
public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {
    @SuppressWarnings("WeakerAccess")
    static final int DATABASE_VERSION = 1;
    @SuppressWarnings("WeakerAccess")
    static final String DATABASE_NAME = "FefesBlogDatabase.db";

    public SQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        checkNotNull(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Nothing to do so far
    }
}
