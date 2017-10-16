package tv.freetel.pmovies2.view;

import android.os.Bundle;
import tv.freetel.pmovies2.R;

/**
 * This is the main launcher Activity for the app. This Activity registers an intent-filter with launcher app.
 *
 */

public class MainActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragmentGrid())
                    .commit();
        }
    }

}