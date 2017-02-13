package de.timbolender.fefereader.db;

import android.provider.BaseColumns;

/**
 * Contract class to unify access to database.
 */
class SQLiteFefesBlogContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SQLiteFefesBlogContract() {}

    public static final String SQL_CREATE_POST_TABLE =
        "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +
            PostEntry._ID + " TEXT PRIMARY KEY," +
            PostEntry.COLUMN_NAME_TIMESTAMP_ID + " TEXT," +
            PostEntry.COLUMN_NAME_IS_READ + " BOOLEAN DEFAULT 0," +
            PostEntry.COLUMN_NAME_IS_UPDATED + " BOOLEAN DEFAULT 0," +
            PostEntry.COLUMN_NAME_CONTENTS + " TEXT," +
            PostEntry.COLUMN_NAME_DATE + " TEXT)";

    /* Inner class that defines the table contents */
    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_TIMESTAMP_ID = "timestamp_id";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_IS_UPDATED = "is_updated";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
