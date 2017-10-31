package tv.freetel.pmovies2.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import retrofit.Response;
import retrofit.Retrofit;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.network.model.MovieInfo;

import static android.app.PendingIntent.getActivity;

/**
 * This is the main launcher Activity for the app. This Activity registers an intent-filter with launcher app.
 *
 */

public class MainActivity extends ParentActivity implements MoviesFragmentGrid.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_order_default));

        mSortCriteria = sortBy;
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragmentGrid())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_order_default));


        String sortCriteria = sortBy;
        // update the location in our second pane using the fragment manager
        if (sortCriteria != null && !sortCriteria.equals(mSortCriteria)) {

            MoviesFragmentGrid ff = (MoviesFragmentGrid) getSupportFragmentManager().findFragmentById(R.id.moviesGrid);
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

            Intent intent = new Intent(this, ShowDetailsActivity.class)
                    .setData(movieUri)
                    .putExtra(ShowDetailsActivity.EXTRA_MOVIE, movieID);
                        startActivity(intent);

    }
}

