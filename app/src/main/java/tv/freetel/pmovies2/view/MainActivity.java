package tv.freetel.pmovies2.view;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.sync.MovieSyncAdapter;

/**
 * This is the main launcher Activity for the app. This Activity registers an intent-filter with launcher app.
 *
 */

public class MainActivity extends ParentActivity implements MoviesFragmentGrid.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private boolean mTwoPane;
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkGPS(); //ensures that the app can't be used without a successful GPS check

        mSortCriteria = MovieSyncAdapter.sortBy(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new DetailsScreenFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        //initialize the sync adapter
        MovieSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //ensures that if the user returns to the running app through some other means, such as through the back button, the GPS check is still performed
        checkGPS();

        String sortCriteria = MovieSyncAdapter.sortBy(this);

        // update the location in our second pane using the fragment manager
        if (sortCriteria != null && !sortCriteria.equals(mSortCriteria)) {

            MoviesFragmentGrid ff = (MoviesFragmentGrid) getSupportFragmentManager().findFragmentById(R.id.movie_grid_container);
            if (null != ff) {
                ff.onSortCriteriaChanged();
            }

//            DetailsFragment df = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
//            if (null != df) {
//                df.onSortCriteriaChanged();
//            }
            mSortCriteria = sortCriteria;
        }
    }

    @Override
    public void onItemSelected(Uri movieUri, int movieID) {
        if (mTwoPane) {
//             In two-pane mode, show the detail view in this activity by
//             adding or replacing the detail fragment using a
//             fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailsScreenFragment.DETAIL_URI, movieUri);
            args.putInt(DetailsScreenFragment.MOVIE_ID, movieID);

            DetailsScreenFragment fragment = new DetailsScreenFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MOVIEFRAGMENT_TAG)
                    .commit();// Commit the transaction
        } else {
            Intent intent = new Intent(this, ShowDetailsActivity.class)
                    .setData(movieUri)
                    .putExtra(ShowDetailsActivity.EXTRA_MOVIE, movieID);
            startActivity(intent);
        }
    }

    /**
     * Checks for Google Play Services (GPS). If GPS is not installed, user will see a pop-up to install GPS from Google Play.
     */
    private void checkGPS() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
            dialog.show();
        }
    }

}

