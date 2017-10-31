package tv.freetel.pmovies2.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 * Needed for ContentProvider: Inside of our MovieContract class, we need to define a few properties: a content authority,
 * which is a unique identifier for our database, a base URI, and path names for each table:
 */

public class MovieContract {

    /**Needed for ContentProvider:
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     * https://guides.codepath.com/android/Creating-Content-Providers
     */
    public static final String CONTENT_AUTHORITY = "tv.freetel.pmovies2";

    /**
     * Needed for ContentProvider: The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */
    public static final String PATH_MOVIE = "movie";

    /**  Needed for ContentProvider: Within MovieContract, create a public static final class called MovieEntry that implements BaseColumns
     * create a class that extends from BaseColumns, which includes an _ID string that is used for the auto increment
     * id of each table.
     */

    /**
     * Create one class for each table that handles all information regarding the table schema and
     * the URIs related to it.
     */

    public static final class MovieEntry implements BaseColumns {

        /** Needed for ContentProvider:
         * Content URI represents the base location for the table
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Needed for ContentProvider:
        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_MOVIE;

        // Create a public static final String call TABLE_NAME with the value "favorite_movie"
        /* Used internally as the name of our weather table. */
        public static final String TABLE_NAME = "favorite_movie";

        // movie id as returned by API
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //      Create a public static final String call COLUMN_TITLE with the value "title"
        public static final String COLUMN_TITLE = "title";

        //      Create a public static final String call COLUMN_POSTER_PATH with the value "poster_path"
        public static final String COLUMN_POSTER_PATH = "poster_path";

        //      Create a public static final String call COLUMN_OVERVIEW with the value "overview"
        public static final String COLUMN_OVERVIEW = "overview";

        //      Create a public static final String call COLUMN_VOTE_AVERAGE with the value "vote_average"
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //      Create a public static final String call COLUMN_RELEASE_DATE with the value "release_date"
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_IS_POPULAR = "isPopular";
        public static final String COLUMN_IS_RATED = "isRated";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";

        // Needed for ContentProvider: Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
