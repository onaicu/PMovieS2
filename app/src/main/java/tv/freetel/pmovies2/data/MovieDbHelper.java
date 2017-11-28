package tv.freetel.pmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import tv.freetel.pmovies2.data.MovieContract.MovieEntry;

/**
 * Manages a local database for weather data.
 */
// Extend SQLiteOpenHelper from MOvieDbHelper
public class MovieDbHelper extends SQLiteOpenHelper {

//  Create a public static final String called DATABASE_NAME with value "favoriteMovie.db"
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "movieList.db";
    //  Increment the database version after altering the behavior of the table
    //Increment the database version after changing the create table statement

//  Create a private static final int called DATABASE_VERSION and set it to 1
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */

    private static final int DATABASE_VERSION = 19;

    //  Create a constructor that accepts a context and call through to the superclass constructor

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * There are two required implementations for this class: onCreate() and onUpgrade().
     * These are fairly self explanatory, the first is called when the database is created,
     * and the second is called anytime DATABASE_VERSION is incremented. For this example, we will not handle
     * updating the database:
     */

    /**
     * Called when the database is first created.
     * @param db The database being created, which all SQL statements will be executed on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        addMovieTable(db);
    }

    /**
     * Inserts the movie table into the database
     * @param sqLiteDatabase The SQLiteDatabase the table is being inserted into.
     */

    private void addMovieTable (SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        // Append NOT NULL to each column's type declaration except for the _ID
                /*
                 * MovieEntry did not explicitly declare a column called "_ID". However,
                 * MovieEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 * columns can be null in order to avoid:
                 * e.g. SQLiteConstraintException: NOT NULL constraint failed: favorite_movie.movie_id (code 1299)
                 */
                        MovieEntry._ID              + " INTEGER PRIMARY KEY ON CONFLICT IGNORE, " + // For the INSERT and UPDATE commands, the keywords "ON CONFLICT" are replaced by "OR", this is to avoid unique constraint exception

                        MovieEntry.COLUMN_MOVIE_ID   + " INTEGER UNIQUE, "                 +
                        MovieEntry.COLUMN_TITLE      + " TEXT NOT NULL, "           +
                        MovieEntry.COLUMN_POSTER_PATH + " TEXT, "          +
                        MovieEntry.COLUMN_OVERVIEW   + " TEXT, "           +
                        MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, "          +
                        MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "          +
                        MovieEntry.COLUMN_IS_POPULAR + " INTEGER, " +    // SQLite does not have a separate Boolean storage class.
                        MovieEntry.COLUMN_IS_RATED + " INTEGER, " +     // Instead, Boolean values are stored as integers 0 (false) and 1 (true).
                        MovieEntry.COLUMN_IS_FAVORITE + " INTEGER )" );
    }

    //  Override onUpgrade, but don't do anything within it yet
    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Within onUpgrade, drop the weather table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        // Tcall onCreate and pass in the SQLiteDatabase (passed in to onUpgrade)
        onCreate(sqLiteDatabase);
    }
}
