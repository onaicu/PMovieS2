package tv.freetel.pmovies2.view;
import android.os.Bundle;
import tv.freetel.pmovies2.R;

/**
 * This Activity is used to show movie details.
 */

public class ShowDetailsActivity
        extends ParentActivity {

    public static final String EXTRA_MOVIE = "tv.freetel.pmovies1.EXTRA_MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailsContainer, new DetailsScreenFragment())
                    .commit();
        }
    }

}
