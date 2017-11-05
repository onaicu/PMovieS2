package tv.freetel.pmovies2.view;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.adapter.GalleryItemAdapter;
import tv.freetel.pmovies2.data.MovieContract;
import tv.freetel.pmovies2.event.MovieEvent;
import tv.freetel.pmovies2.network.model.Movie;
import tv.freetel.pmovies2.network.model.MovieInfo;
import tv.freetel.pmovies2.network.service.DiscoverMovieService;
import tv.freetel.pmovies2.sync.MovieSyncAdapter;
import tv.freetel.pmovies2.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragmentGrid extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //VARIABLES**************************************************************
    private static final String LOG_TAG = MoviesFragmentGrid.class.getSimpleName();

    //LAYOUTS**************************************************************
    @Bind(R.id.moviesGrid)
    GridView mMovieGrid;

    //ADAPTERS**************************************************************
    private GalleryItemAdapter mFavoriteMovieAdapter;
    private List<Movie> mMovieList = new ArrayList<>();
    private static final int MOVIE_LOADER_ID = 0;

    // For the main Grid layout view, we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER_PATH = 2;

    public MoviesFragmentGrid() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true); // fragment should handle menu events
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * fetch movie list from Open Movie DB REST back-end.
     * The sort order is retrieved from Shared Preferences
     private void fetchMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_order_default));
        getMovies(sortBy);}
    */

    public static String sortBy(Context context){

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(context.getString(R.string.pref_sort_key),
            context.getString(R.string.pref_sort_order_default));
    }

    /**
     * Used to make a async call to movies DB to fetch a list of popular movies.
     */
    public void getMovies(String sortBy) {


        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<MovieInfo> restCall = api.getMovies(sortBy, Constants.MOVIE_DB_API_KEY);

        restCall.enqueue(new retrofit.Callback<MovieInfo>() {
            @Override
            public void onResponse(Response<MovieInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)
                    MovieInfo movieInfo = response.body();
                    mMovieList = movieInfo.getmMovieList();
                    //fetch data from database in case favorite is selected from settings sort criteria. See lines 324 onsortcriteria and 101 Onstart.
                    insertMovieRecords(mMovieList);
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    Log.d(LOG_TAG, "Web call error");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Web call error. exception: " + t.toString()+ "...printing stack trace below \\n");
                t.printStackTrace();
            }
        });
    }

    /**
     * Inserts movie JSON result into movie.db DB.
     *
     */
    private void insertMovieRecords(final List<Movie> movieList) {
        // Insert the new movie information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieList.size());

        for (Movie movie : movieList) {

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry._ID, movie.getmId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getmTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getmVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getmReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getmOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getmPosterPath());
            cVVector.add(movieValues);

        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "DB Complete. " + inserted + " Inserted");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() called");
        // The CursorAdapter will take data from our cursor and populate the GridView.
        mFavoriteMovieAdapter = new GalleryItemAdapter(getActivity(),null, 0) ; //Comment out new ArrayList<Movie>()//)

        View view = inflater.inflate(R.layout.fragment_movies_fragment_grid, container, false);
        ButterKnife.bind(this, view);

        //attach the adapter to the GridView
        mMovieGrid.setAdapter(mFavoriteMovieAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated called");
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }


    /**
     * Create a Cursor Loader : onCreateLoader, onLoadFinished, onLoadReset to query data of favorit movies.
     *
     * @param id
     * @param args
     * @
     * +
     */

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "onCreateLoader called");
            // Defines a string to contain the selection clause
            String selectionClause = null;
            // An array to contain selection arguments
            String[] selectionArgs = null;
            // Gets a word from the UI
            String sortCriteria = this.sortBy(getContext());

            // Construct a selection clause that matches the word that the user entered.
            if (sortCriteria.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_popular))) {
                selectionClause = MovieContract.MovieEntry.COLUMN_IS_POPULAR + " = ?";
            } else if (sortCriteria.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_rating))) {
                selectionClause = MovieContract.MovieEntry.COLUMN_IS_RATED + " = ?";
            } else {
                selectionClause = MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?";
            }

            // Use the user's input string as the (only) selection argument.
            selectionArgs = new String[]{"" + 1};

            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,   //projection
                    selectionClause,  //selection
                    selectionArgs,  //selection args
                    null); //sort order
        }

        /**
         * This callback makes the fragment visible to the user when the containing activity is started.
         * We want to make a network request before user can  begin interacting with the user (onResume callback)
         */
        @Override
        public void onStart() {
            super.onStart();
            Log.d(LOG_TAG, "onStart called");
            //if  user has selected either "popular" or "highest rated" sort criteria, we need to make a web call
            if (this.sortBy(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
                fetchMovies();
            }
        }

        /**
         * Used to fire an event to the Bus that will fetch movie list from Open Movie DB REST back-end.
         * The sort order is retrieved from Shared Preferences
         */
        private void fetchMovies() {
            MovieSyncAdapter.syncImmediately(getContext());
        }

    /**
     * Called when loader is complete and data is ready. Used for making UI updates.
     *
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished called cursor count is " + cursor.getCount() + " mFavoriteMovieAdapter is: " + mFavoriteMovieAdapter);
        if (mFavoriteMovieAdapter != null) {
            mFavoriteMovieAdapter.swapCursor(cursor);
        }
    }

    /**
     * Called when loader is destroyed. Release resources
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset called");
        if (mFavoriteMovieAdapter != null) {
            mFavoriteMovieAdapter.swapCursor(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(LOG_TAG, "onCreateOptionsMenu() called");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.gallery_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected() called");
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop called");
    }


    /**
     * Used to navigate to Details screen through explicit intent.
     *
     * @param position grid item position clicked by the user.
     */
    @OnItemClick(R.id.moviesGrid)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

        Log.d(LOG_TAG, "Grid view item clicked: position: " + position + " movie ID: " + cursor.getInt(COL_MOVIE_ID) + " movie title: " + cursor.getString(COL_MOVIE_TITLE) + " poster path: " + cursor.getString(COL_MOVIE_POSTER_PATH));

        if (cursor != null) {
            ((Callback) getActivity())
                    .onItemSelected(MovieContract.MovieEntry.buildMovieUri(
                            cursor.getInt(COL_MOVIE_ID)
                    ), cursor.getInt(COL_MOVIE_ID));
        }
    }

    /**
     * This method is triggered when we have updated the local DB with back-end results.
     * Restart the loader.  restartLoader will trigger onCreateLoader to be called again.
     */
    @Subscribe
    public void onMovieEvent(MovieEvent movieEvent) {
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }


    void onSortCriteriaChanged() {
        Log.d(LOG_TAG, "onSortCriteriaChanged() called");

        //make a web call if the user selected popular or highly rated
        if (!this.sortBy(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
            fetchMovies();
        } else {
            //if the user selected favorites, just show movies in the local DB
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri, int movieID);
    }
}