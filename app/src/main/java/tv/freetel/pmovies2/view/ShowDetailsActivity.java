package tv.freetel.pmovies2.view;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import tv.freetel.pmovies2.R;

/**
 * This Activity is used to show movie details.
 */

public class ShowDetailsActivity
        extends ParentActivity {

    private static final String LOG_TAG = ShowDetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "tv.freetel.pmovies2.EXTRA_MOVIE";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private DetailsScreenFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

            FragmentManager fm = getSupportFragmentManager();
            mDetailsFragment = (DetailsScreenFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

            // If the Fragment is non-null, then it is currently being
            // retained across a configuration change.
        if (mDetailsFragment == null) {
            mDetailsFragment = new DetailsScreenFragment();
            Bundle arguments = new Bundle();
            int movieId = getIntent().getIntExtra(ShowDetailsActivity.EXTRA_MOVIE, -1);
            arguments.putInt(DetailsScreenFragment.MOVIE_ID, movieId);
            arguments.putParcelable(DetailsScreenFragment.DETAIL_URI, getIntent().getData());
            mDetailsFragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.movie_details_container, mDetailsFragment, TAG_TASK_FRAGMENT).commit();
        }

    }
}


