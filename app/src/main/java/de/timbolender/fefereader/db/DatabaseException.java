package de.timbolender.fefereader.db;

/**
 * Exception about an error in the database.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException () {
    }

    public DatabaseException (String message) {
        super (message);
    }

    public DatabaseException (Throwable cause) {
        super (cause);
    }

    public DatabaseException (String message, Throwable cause) {
        super (message, cause);
    }
}
