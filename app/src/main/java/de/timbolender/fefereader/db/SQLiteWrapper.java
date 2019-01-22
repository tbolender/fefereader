package de.timbolender.fefereader.db;

import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.timbolender.fefereader.db.SQLiteFefesBlogContract.PostEntry;

/**
 * Allows loading of posts from SQLite database.
 */
public class SQLiteWrapper implements DatabaseWrapper {
    private static final String TAG = SQLiteWrapper.class.getSimpleName();

    private final SQLiteDatabase database;

    public SQLiteWrapper(SQLiteDatabase database) {
        checkNotNull(database);

        this.database = database;
    }

    @Override
    public long getUnreadPostCount() throws DatabaseException {
        try {
            String selection = PostEntry.COLUMN_NAME_IS_READ + " = 0";
            return DatabaseUtils.queryNumEntries(database, PostEntry.TABLE_NAME, selection);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public long getUpdatedPostCount() throws DatabaseException {
        try {
            String selection = PostEntry.COLUMN_NAME_IS_UPDATED + " = 1 AND " + PostEntry.COLUMN_NAME_IS_READ + " = 1";
            return DatabaseUtils.queryNumEntries(database, PostEntry.TABLE_NAME, selection);
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
