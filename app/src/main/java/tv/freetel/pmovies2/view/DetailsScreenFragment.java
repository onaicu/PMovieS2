package tv.freetel.pmovies2.view;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.adapter.MovieReviewAdapter;
import tv.freetel.pmovies2.adapter.MovieTrailerAdapter;
import tv.freetel.pmovies2.data.MovieContract;
import tv.freetel.pmovies2.network.model.Movie;
import tv.freetel.pmovies2.network.model.MovieReview;
import tv.freetel.pmovies2.network.model.ReviewInfo;
import tv.freetel.pmovies2.network.model.Trailer;
import tv.freetel.pmovies2.network.model.TrailerInfo;
import tv.freetel.pmovies2.network.service.DiscoverMovieService;
import tv.freetel.pmovies2.sync.MovieSyncAdapter;
import tv.freetel.pmovies2.util.Constants;


/**
 * This Fragment class is added by ShowDetailsActivity to show details screen
 * <p>
 * The classes and interfaces of the Loader API:
 * https://www.grokkingandroid.com/using-loaders-in-android/
 * Add implements LoaderManager.LoaderCallbacks<Cursor> in order to be able to insert movie into favorite movie db.
 * The classes and interfaces of the Loader API
 * LoaderManager -Manages your Loaders for you. Responsible for dealing with the Activity or Fragment lifecycle
 * LoaderManager.LoaderCallbacks-A callback interface you must implement
 * Loader-The base class for all Loaders
 * AsyncTaskLoader -An implementation that uses an AsyncTask to do its work
 * CursorLoader-A subclass of AsyncTaskLoader for accessing ContentProvider data
 */
public class DetailsScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String MOVIE_ID = "ID";
    static final String DETAIL_URI = "URI";
    private static final String LOG_TAG = DetailsScreenFragment.class.getSimpleName();
    private static final String MOVIE_DETAILS_SHARE_HASHTAG = " #PopularMoviesApp";
    private static final int DETAIL_LOADER = 0;

    // Since the details screen shows all movie attributes, define a projection that contains
    // all columns from the movie db table
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE
    };


    /**
     * these constants correspond to the projection defined above, and must change if the projection changes
     */
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_VOTE_AVERAGE = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_POSTER_PATH = 5;
    private static final int COL_MOVIE_IS_FAVORITE = 6;

    /**
     * LAYOUTS**************************************************************
     */

    @Bind(R.id.movieTitle)
    TextView mMovieTitleTxtV;
    @Bind(R.id.moviePoster)
    ImageView mMoviePosterImV;
    @Bind(R.id.movieOverview)
    TextView mMovieOverviewTxtV;
    @Bind(R.id.movieRating)
    TextView mMovieRatingTxtV;
    @Bind(R.id.movieReleaseYear)
    TextView mMovieReleaseYearTxtV;
    @Bind(R.id.favoriteIcon)
    ImageView mMovieFavorite;

    /**
     * declare movie review and trailer adapter and bind it inside of the oncreateview of this class.
     * Recyclerview is vertical oriented for reviews and horizontal oriented for recyclerviews.
     * References to RecyclerView and bind it to the
     */
    MovieReviewAdapter movieReviewAdapter;
    /* Using Bind, we get a reference to our RecyclerView from xml. This allows us to
    *do things like set the adapter of the RecyclerView and toggle the visibility. RV=recycling view layout
    */
    @Bind(R.id.movieReviewsRV)
    RecyclerView mMovieReviewRV;
    MovieTrailerAdapter movieTrailerAdapter;
    @Bind(R.id.movieTrailersRV)
    RecyclerView mTrailerRV;

    //VARIABLES**************************************************************
    // declare global
    Movie selectedMovie;
    private Uri mUri;
    private int mMovieId;
    //VARIABLES**************************************************************
    private int mMovieID;
    private String mMovieTitle;
    private String mMoviePoster;
    private String mMovieOverview;
    private String mMovieRating;
    private String mMovieReleaseYear;
    private boolean mIsFavorite;

    public DetailsScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// fragment should handle menu events.
        setRetainInstance(true);// Retain this fragment across configuration changes.

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop called");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            mMovieId = arguments.getInt(MOVIE_ID);
        }

        View view = inflater.inflate(R.layout.fragment_details_screen, container, false);
        ButterKnife.bind(this, view);
        mMovieReviewRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailerRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMovieReviewRV.setHasFixedSize(true);
        mTrailerRV.setHasFixedSize(true);

        movieReviewAdapter = new MovieReviewAdapter(null);
        movieTrailerAdapter = new MovieTrailerAdapter(null,getContext());

        /*
        * The MovieReviewAdapter is responsible for displaying each item in the list.
        */
        mMovieReviewRV.setAdapter(movieReviewAdapter);
        mTrailerRV.setAdapter(movieTrailerAdapter);

        fillDetailsScreen();

        getTrailers(mMovieId);
        getReviews(mMovieId);

        return view;
    }

    /**
     * Used to make a async call to movies DB to fetch reviews for a specific movie. Done in DetailsScreenFragment
     * because reviews appear in details view of a movie the getReview function needs to be declared
     * here in DetailsScreenFragement
     */

    public void getReviews(int movieId) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<ReviewInfo> restCall = api.getReviews(movieId, Constants.MOVIE_DB_API_KEY);

        Log.d(LOG_TAG, "Making REST call to fetch movie reviews. Movie ID: " + movieId);
        restCall.enqueue(new Callback<ReviewInfo>() {
            @Override
            public void onResponse(Response<ReviewInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)
                    ReviewInfo movieReviews = response.body();
                    movieReviewAdapter.addAll(movieReviews.getmReviewList());
                    Log.d(LOG_TAG, "Reviews Result count : " + movieReviews.getmReviewList().size());
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    Log.d(LOG_TAG, "Web call error while fetching movie reviews.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Web call error to get reviews. exception: " + toString());
            }
        });
    }


    /**
     * Used to make a async call to movies DB to fetch trailers for a specific movie. Done in DetailsScreenFragment
     * because trailers appear in details view of a movie the getTrailer function needs to be declared
     * here in DetailsScreenFragement
     */

    public void getTrailers(int movieId) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<TrailerInfo> restCall = api.getTrailers(movieId, Constants.MOVIE_DB_API_KEY);

        Log.d(LOG_TAG, "Making REST call to fetch movie reviews. Movie ID: " + movieId);
        restCall.enqueue(new Callback<TrailerInfo>() {
            @Override
            public void onResponse(Response<TrailerInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)
                    TrailerInfo movieTrailers = response.body();
                    movieTrailerAdapter.addAll(movieTrailers.getmResults());
                    Log.d(LOG_TAG, "Trailer Result count : " + movieTrailers.getmResults());
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    Log.d(LOG_TAG, "Web call error while fetching movie trailers.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Web call error to get trailers. exception: " + toString());
            }
        });
    }

    /*You do not instantiate the LoaderManager yourself. Instead you simply call
    getLoaderManager()from within your activity or your fragment to get hold of it.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //The initLoader() method adds a Loader to the LoaderManager:
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    //Create a Cursor Loader : onCreateLoader, onLoadFinished, onLoadReset to query data of favorit movies.

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            String selectionClause = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = new String[]{"" + mMovieId};

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new android.support.v4.content.CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,      //projection
                    selectionClause,    //selection
                    selectionArgs,      //selection args
                    null                //sort order
            );
        }
        return null;
    }

    //Here you update the UI based on the results of your query.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!cursor.moveToFirst()) {
            return;
        }

        mMovieID = cursor.getInt(COL_MOVIE_ID);
        mMovieTitle = cursor.getString(COL_MOVIE_TITLE);
        mMovieOverview = cursor.getString(COL_MOVIE_OVERVIEW);
        mMovieRating = cursor.getString(COL_MOVIE_VOTE_AVERAGE);
        mMovieReleaseYear = cursor.getString(COL_MOVIE_RELEASE_DATE);
        mMoviePoster = cursor.getString(COL_MOVIE_POSTER_PATH);
        mIsFavorite = cursor.getInt(COL_MOVIE_IS_FAVORITE) > 0;

        fillDetailsScreen();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Used to render original title, poster image, overview (plot), user rating and release date.
     */
    public void fillDetailsScreen() {

        if (mMovieTitleTxtV != null) {
            mMovieTitleTxtV.setText(mMovieTitle);
        }

        if (mMoviePosterImV != null) {
            Picasso.with(getContext())
                    .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMoviePoster)
                    .placeholder(R.drawable.poster_placeholder) // support download placeholder
                    .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                    .into(mMoviePosterImV);
        }

        if (mMovieOverviewTxtV != null) {
            mMovieOverviewTxtV.setText(mMovieOverview);
        }

        //we only want to display ratings rounded up to 3 chars max (e.g. 6.3)
        if (mMovieRating != null && mMovieRating.length() >= 3) {
            mMovieRating = mMovieRating.substring(0, 3);
        }
        if (mMovieRatingTxtV != null) {
            mMovieRatingTxtV.setText("" + mMovieRating + "/10");
        }

        if (mMovieReleaseYear != null) {
            // Movie DB API returns release date in yyyy--mm-dd format
            // Extract the year through regex
            Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
            Matcher dateMatcher = datePattern.matcher(mMovieReleaseYear);
            if (dateMatcher.find()) {
                mMovieReleaseYear = dateMatcher.group(1);

            }
        }

        if (mMovieReleaseYearTxtV != null) {
            mMovieReleaseYearTxtV.setText(mMovieReleaseYear);
        }


        if (mMovieFavorite != null) {
            if (mIsFavorite) {
                mMovieFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                mMovieFavorite.setVisibility(View.VISIBLE);
            } else {
                mMovieFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                mMovieFavorite.setVisibility(View.VISIBLE);
            }

                mMovieFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseCrash.log("Movie ID "+mMovieID+" saved as Favorite");
                        // Create and execute the background task.

                        DBUpdateTask task = new DBUpdateTask(mIsFavorite, mMovieID);
                        task.execute();
                    }
                });

        }
            fetchMovieTrailersAndReviews(mMovieID);

    }

    private void fetchMovieTrailersAndReviews(final int movieId) {
        getReviews(movieId);
        getTrailers(movieId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    /**
     * Returns an implicit intent to launch another app. Movie title is added as intent extra.
     *
     * @return intent
     */
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND); //generic action
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //required to return to Popular Movies app
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieTitleTxtV + MOVIE_DETAILS_SHARE_HASHTAG);
        return shareIntent;
    }

    private void setupMovieReviewAdapter(final List<MovieReview> movieReviews) {
        //if (getActivity() == null || mMovieReviewList == null) return;
        if (getActivity() == null) return;

        if (movieReviews != null) {
            movieReviewAdapter.setmMovieReviewList(movieReviews);
            movieReviewAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "# of reviews in setupAdapet is: " + movieReviews.size());
        }
    }

    private void setupMovieTrailerdapter(final List<Trailer> movieTrailers) {
        if (getActivity() == null) return;

        if (movieTrailers != null) {
            movieTrailerAdapter.setmMovieTrailerList(movieTrailers);
            movieTrailerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Used to insert a record into SQLite DB in a non-UI worker thread.
     */
    private class DBUpdateTask extends AsyncTask<Void, Integer, Void> {

        boolean mIsFavorite;
        int movieID;

        DBUpdateTask(boolean mIsFavorite, int movieID) {
            this.mIsFavorite = mIsFavorite;
            this.movieID = movieID;
        }


        @Override
        protected void onPreExecute() {
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... ignore) {
            ContentValues updateValues = new ContentValues();
            if (mIsFavorite) {
                updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            } else {
                updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            }

            // Defines selection criteria for the rows you want to update
            String selectionClause = MovieContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + movieID};

            // Defines a variable to contain the number of updated rows
            int rowsUpdated = 0;


            rowsUpdated = getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,  // the user dictionary content URI
                    updateValues,                       // the columns to update
                    selectionClause,                    // the column to select on
                    selectionArgs);                      // the value to compare to
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(Void ignore) {

        }
    }

}
