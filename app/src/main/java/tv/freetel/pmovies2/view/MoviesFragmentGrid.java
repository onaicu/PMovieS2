package tv.freetel.pmovies2.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.adapter.GalleryItemAdapter;
import tv.freetel.pmovies2.adapter.MovieReviewAdapter;
import tv.freetel.pmovies2.event.DiscoverMovieEvent;
import tv.freetel.pmovies2.event.MovieEvent;
import tv.freetel.pmovies2.event.ReviewEvent;
import tv.freetel.pmovies2.network.model.Movie;
import tv.freetel.pmovies2.network.model.MovieInfo;
import tv.freetel.pmovies2.network.model.MovieReview;
import tv.freetel.pmovies2.network.service.DiscoverMovieService;
import tv.freetel.pmovies2.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragmentGrid extends Fragment {

    private static final String LOG_TAG = MoviesFragmentGrid.class.getSimpleName();

    @Bind(R.id.moviesGrid)
    GridView mMovieGrid;

    private GalleryItemAdapter galleryItemAdapter;
    private List<Movie> mMovieList =new ArrayList<>();

    private MovieReviewAdapter movieReviewAdapter;
    private List<MovieReview> mReviewList =new ArrayList<>();

    public MoviesFragmentGrid() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true); // fragment should handle menu events
    }

    /**
     * This callback makes the fragment visible to the user when the containing activity is started.
     * We want to make a network request before user can  begin interacting with the user (onResume callback)
     */

    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * fetch movie list from Open Movie DB REST back-end.
     * The sort order is retrieved from Shared Preferences
     */
    private void fetchMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_order_default));
        getMovies(sortBy);
    }

    /**
     * Used to make a async call to movies DB to fetch a list of popular movies.
     */
    public void getMovies (String sortBy) {


        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<MovieInfo> restCall = api.getMovies(sortBy,Constants.MOVIE_DB_API_KEY);

        restCall.enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(Response<MovieInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)
                    MovieInfo movieInfo = response.body();
                    mMovieList = movieInfo.getmMovieList();
                    galleryItemAdapter.addAll(mMovieList);
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    Log.d(LOG_TAG, "Web call error");
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.gallery_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public DiscoverMovieEvent produceDiscoverMovieEvent(String queryParam) {
        return new DiscoverMovieEvent(queryParam);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_fragment_grid, container, false);
        ButterKnife.bind(this, view);
        galleryItemAdapter = new GalleryItemAdapter(getActivity(), new ArrayList<Movie>());
        mMovieGrid.setAdapter(galleryItemAdapter);
        return view;
    }

    /**
     * Used to navigate to Details screen through explicit intent.
     *
     * @param position grid item position clicked by the user.
     */
    @OnItemClick(R.id.moviesGrid)
    void onItemClick(int position) {
        Movie selectedMovie =
                mMovieList.get(position);

        Intent intent = new Intent(getContext(), ShowDetailsActivity.class);
        intent.putExtra(ShowDetailsActivity.EXTRA_MOVIE, selectedMovie);
        startActivity(intent);
    }

    @Subscribe
    public void onMovieEvent(MovieEvent movieEvent) {
        mMovieList = movieEvent.getmMovieList();

    }

    @Subscribe
    public void onReviewEvent(ReviewEvent reviewEvent) {
        mReviewList = reviewEvent.getmReviewList();

    }


}