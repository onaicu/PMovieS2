package tv.freetel.pmovies2.view;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.adapter.GalleryItemAdapter;
import tv.freetel.pmovies2.data.MovieContract;
import tv.freetel.pmovies2.sync.MovieSyncAdapter;

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

        Log.d(LOG_TAG, "onStart called");
        //if  user has selected either "popular" or "highest rated" sort criteria, we need to make a web call
        if (MovieSyncAdapter.sortBy(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
            fetchMovies();
        }

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
            String sortCriteria = MovieSyncAdapter.sortBy(getContext());

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
         * Used to fire an event to the Bus that will fetch movie list from Open Movie DB REST back-end.
         * The sort order is retrieved from Shared Preferences
         */
        public void fetchMovies() {
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


    void onSortCriteriaChanged() {
        Log.d(LOG_TAG, "onSortCriteriaChanged() called");

        //make a web call if the user selected popular or highly rated
        if (!MovieSyncAdapter.sortBy(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
            fetchMovies();
            /**
             *because the GalleryItemAdapter/favorite adapter accepts only cursors to display data on the ui, we need to
             *call the getLoaderManager method also for the fetchMovies case. The fetchMovies () method is fetching the data from
             *the api through the getMovies method called in the MovieSyncAdapter and then stores the data in the database through the
             *insertmovies method (located in MovieSyncApadter). However in order to diplay it properly on the UI whenever the sortcriteria
             *is selected, the stored data needs to be displayed through a loader.
             */
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
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