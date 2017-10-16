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
    public static final String DATABASE_NAME = "favoriteMovie.db";
    //  Increment the database version after altering the behavior of the table
    //Increment the database version after changing the create table statement

//  Create a private static final int called DATABASE_VERSION and set it to 1
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */

    private static final int DATABASE_VERSION = 2;

    //  Create a constructor that accepts a context and call through to the superclass constructor

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//  Override onCreate and create the weather table from within it
    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our favoriteMovie data.
         */
        final String SQL_CREATE_FAVORITE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
// Append NOT NULL to each column's type declaration except for the _ID
                /*
                 * MovieEntry did not explicitly declare a column called "_ID". However,
                 * MovieEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        MovieEntry._ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieEntry.COLUMN_MOVIE_ID   + " INTEGER NOT NULL, "                 +
                        MovieEntry.COLUMN_TITLE      + " TEXT NOT NULL, "           +
                        MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "          +
                        MovieEntry.COLUMN_OVERVIEW   + " TEXT NOT NULL, "           +
                        MovieEntry.COLUMN_VOTE_AVERAGE + "REAL NOT NULL, "          +
                        MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "         + ");";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
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
