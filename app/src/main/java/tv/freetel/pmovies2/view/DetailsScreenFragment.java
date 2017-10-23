package tv.freetel.pmovies2.view;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatCheckBox;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
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
import tv.freetel.pmovies2.util.Constants;

import static tv.freetel.pmovies2.data.MovieContract.MovieEntry.CONTENT_URI;

/**
 * This Fragment class is added by ShowDetailsActivity to show details screen
 *
 *The classes and interfaces of the Loader API:
 *https://www.grokkingandroid.com/using-loaders-in-android/
 * Add implements LoaderManager.LoaderCallbacks<Cursor> in order to be able to insert movie into favorite movie db.
 * The classes and interfaces of the Loader API
 * LoaderManager -Manages your Loaders for you. Responsible for dealing with the Activity or Fragment lifecycle
 * LoaderManager.LoaderCallbacks-A callback interface you must implement
 * Loader-The base class for all Loaders
 * AsyncTaskLoader -An implementation that uses an AsyncTask to do its work
 * CursorLoader-A subclass of AsyncTaskLoader for accessing ContentProvider data
 */
public class DetailsScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsScreenFragment.class.getSimpleName();
    private static final String MOVIE_DETAILS_SHARE_HASHTAG = " #PopularMoviesApp";
    private static final int DETAIL_LOADER = 0;
    private URI mCurrentMovieUri;

    //LAYOUTS**************************************************************

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

    //ADAPTERS**************************************************************
    /**
     * declare movie review and trailer adapter and bind it inside of the oncreateview of this class.
     * Recyclerview is vertical oriented for reviews and horizontal oriented for recyclerviews.
     * References to RecyclerView and bind it to the
     */
    MovieReviewAdapter movieReviewAdapter;
    /* Using Bind, we get a reference to our RecyclerView from xml. This allows us to
    *do things like set the adapter of the RecyclerView and toggle the visibility. RV=recycling view layout
    */
    @Bind(R.id.movieReviewsRV) RecyclerView mMovieReviewRV;

    MovieTrailerAdapter movieTrailerAdapter;
    @Bind(R.id.movieTrailersRV) RecyclerView mTrailerRV;

    //VARIABLES**************************************************************
    private int mMovieID;
    private String mMovieTitle;
    private String mMoviePoster;
    private String mMovieOverview;
    private String mMovieRating;
    private String mMovieReleaseYear;
    AppCompatCheckBox FavoriteIconView;

    public DetailsScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// fragment should handle menu events.
        setRetainInstance(true);// Retain this fragment across configuration changes.
           }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details_screen, container, false);
        ButterKnife.bind(this, view);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * Since the constructor LinearLayoutManager uses the activity as the parameter (not the fragment),
         * a Tabs Activity stays active during tabs changes.
         *
         * Removing the local field in mLinearLayoutManager from the class, and using a weak reference,
         * I could get rid of this problem:“LayoutManager is already attached to a RecyclerView” error
         */

        mMovieReviewRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailerRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

        mMovieReviewRV.setHasFixedSize(true);
        mTrailerRV.setHasFixedSize(true);

        //Initialize the review adapter with Arraylist because List is a abstract class.
        movieReviewAdapter = new MovieReviewAdapter(new ArrayList<MovieReview>());
        movieTrailerAdapter = new MovieTrailerAdapter(new ArrayList<Trailer>(),getContext());

        /*
         * The MovieReviewAdapter is responsible for displaying each item in the list.
         */
        mMovieReviewRV.setAdapter(movieReviewAdapter);

        //Set adapter to inflate the movie trailer reviews.
        mTrailerRV.setAdapter(movieTrailerAdapter);

        //Parent activity is started by firing-off an explicit intent.
        //Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(ShowDetailsActivity.EXTRA_MOVIE)) {
            Movie selectedMovie = intent.getParcelableExtra(ShowDetailsActivity.EXTRA_MOVIE);
            if (selectedMovie != null) {
                mMovieTitle = selectedMovie.getmTitle();
                fillDetailScreen(selectedMovie);
            }
        }

        return view;
    }

    /**
     * Used to render original title, poster image, overview (plot), user rating, review and release date.
     *
     * @param selectedMovie
     */
    private void fillDetailScreen(final Movie selectedMovie) {
        mMovieTitleTxtV.setText(selectedMovie.getmTitle());
        Picasso.with(getContext())
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + selectedMovie.getmPosterPath())
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(mMoviePosterImV);
        mMovieRatingTxtV.setText("" + selectedMovie.getmVoteAverage() + "/10");
        mMovieOverviewTxtV.setText(selectedMovie.getmOverview());

        //Set the reviews to details screen fragment.
        getReviews(selectedMovie.getmId());

        //Set the trailers to details screen fragment.
        getTrailers(selectedMovie.getmId());

        // Movie DB API returns release date in yyyy--mm-dd format
        // Extract the year through regex
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        String year = selectedMovie.getmReleaseDate();
        Matcher dateMatcher = datePattern.matcher(year);
        if (dateMatcher.find()) {
            year = dateMatcher.group(1);

        }
        mMovieReleaseYearTxtV.setText(year);
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

    public void getTrailers (int movieId) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<TrailerInfo> restCall = api.getTrailers(movieId, Constants.MOVIE_DB_API_KEY);

        Log.d(LOG_TAG, "Making REST call to fetch movie reviews. Movie ID: " + movieId);
        restCall.enqueue(new Callback <TrailerInfo>() {
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

    /**
     * include a check box in your DetailActivity and when that checkbox is clicked you can insert
     * that particular movie in your db and if that check box is unchecked you can remove it from your db
     * https://stackoverflow.com/questions/11131058/how-to-properly-insert-values-into-the-sqlite-database-using-contentproviders-i
     */




    public onCheckboxClicked(View FavoriteIconView) {
        // Is the view now checked?
        boolean checked = ((AppCompatCheckBox) FavoriteIconView).isChecked();

        // Check which checkbox was clicked
        switch(FavoriteIconView.getId()) {
            case R.id.favoriteIcon:
                if (checked){

                    // Add a new movie record
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovieID.getText().toString().trim());

                    values.put(MovieContract.MovieEntry.COLUMN_TITLE,
                            mMovieTitleTxtV.getText().toString().trim());

                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                            mMoviePosterImV.getText().toString().trim());

                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,
                            mMovieOverviewTxtV.getText().toString().trim());

                    values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                            mMovieRatingTxtV.getText().toString().trim());

                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                            mMovieReleaseYearTxtV.getText().toString().trim());

                    Uri uri;
                    uri = getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI, values);

                }

            // Delete movie from favorite movie database
            else {

            // Only perform the delete if this is an existing product.
            if (mCurrentMovieUri != null) {
                // Call the ContentResolver to delete the movie at the given content URI.
                // Pass in null for the selection and selection args because the mCurrentMovieUri
                // content URI already identifies the movie that we want.

                int movieDeleted = getContentResolver().delete(mCurrentMovieUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                if (movieDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, getString(R.string.failed_deleteFavorite),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.deleted_favorite),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Remove the meat
    }}
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
        if (mCurrentMovieUri != null) {
            String selectionClause = MovieContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + mMovieID};

        // Since the details screen shows all movie attributes, define a projection that contains
            // all columns from the movie db table

            String[] MOVIE_COLUMNS = {
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                    MovieContract.MovieEntry.COLUMN_TITLE,
                    MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                    MovieContract.MovieEntry.COLUMN_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                    MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            };

         /* URI for all rows of weather data in our weather table */
            Uri movieQueryUri = CONTENT_URI;
        // This loader will execute the ContentProvider's query method on a background thread
            CursorLoader loader = new CursorLoader(
                    this.getActivity(),
                    movieQueryUri, //projection
                    MOVIE_COLUMNS, //selection
                    selectionClause,    //selection
                    selectionArgs,      //selection args
                    null); //sort order
        };
        return null;
    }

        //Here you update the UI based on the results of your query.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        // Bail early if the cursor is null or there is less than 1 row in the cursor
            if (!cursor.moveToFirst()) {
                return;
            }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int movieIdColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int movieTitleColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int moviePosterColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            int movieOverviewColumIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            int voteAverageColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            int movieReleaseYearColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);

            // Extract out the value from the Cursor for the given column index
        mMovieID = cursor.getInt(movieIdColumnIndex);
        mMovieTitle = cursor.getString(movieTitleColumnIndex);
        mMoviePoster = cursor.getString(moviePosterColumnIndex);
        mMovieOverview = cursor.getString(movieOverviewColumIndex);
        mMovieRating = cursor.getString(voteAverageColumnIndex);
        mMovieReleaseYear = cursor.getString(movieReleaseYearColumnIndex);

        fillDetailsScreen();
    } }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**
         * Used to render original title, poster image, overview (plot), user rating and release date.
         */

        private void fillDetailsScreen(){

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

            fetchMovieTrailersAndReviews(mMovieID);
    }
    }
}
