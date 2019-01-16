package de.timbolender.fefereader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Objects;

import static de.timbolender.fefereader.db.SQLiteFefesBlogContract.SQL_CREATE_POST_TABLE;

/**
 * Helper for initialising database.
 */
public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {
    @SuppressWarnings("WeakerAccess")
    static final int DATABASE_VERSION = 1;
    @SuppressWarnings("WeakerAccess")
    static final String DATABASE_NAME = "FefesBlogDatabase.db";

    public SQLiteOpenHelper(Context context) {
        super(Objects.requireNonNull(context), DATABASE_NAME, null, DATABASE_VERSION);
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
