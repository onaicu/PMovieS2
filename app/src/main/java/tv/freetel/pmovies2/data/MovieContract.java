package tv.freetel.pmovies2.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */

public class MovieContract {
    //  Within MovieContract, create a public static final class called MovieEntry that implements BaseColumns
    public static final class MovieEntry implements BaseColumns {
        // Do steps 2 through 9 within the MovieEntry class

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
    }
}
