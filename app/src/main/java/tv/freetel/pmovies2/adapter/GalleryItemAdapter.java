package tv.freetel.pmovies2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.data.MovieContract;
import tv.freetel.pmovies2.network.model.Movie;
import tv.freetel.pmovies2.util.Constants;

/**
 * This class acts as a bridge between movie grid (a subclass of AdapterView)
 * and the underlying data for the movie Grid. The Adapter provides access to the data items.
 * This Adapter is also responsible for making a View for each item in the data set.
 *
 */
/**public class GalleryItemAdapter extends ArrayAdapter<Movie> {*/

    public class GalleryItemAdapter extends CursorAdapter {

    private static final String LOG_TAG = GalleryItemAdapter.class.getSimpleName();
    private Context mContext;

    /** public GalleryItemAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
        mContext = context;
        mMovieList = movies;
    }*/

    public GalleryItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Picasso.with(mContext).invalidate(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + convertCursorRowToUXFormat(cursor));
        Log.d(LOG_TAG, "Loading image... for movie ID: " + cursor.getInt(0) + " movie title: " + cursor.getString(1) + " poster path: " + cursor.getString(2));

        /**
         * image view item cannot be declared global. It needs to stay local in order to avoid replicating the images
         *from one movie to another.
         */
        ImageView imageView = view.findViewById(R.id.gallery_item_imageView);

        Picasso.with(mContext)
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + convertCursorRowToUXFormat(cursor))
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(imageView);
    }


    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_gallery, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
        Picasso.with(mContext)
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMovieList.get(position).getmPosterPath())
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(imageView);
        return convertView;
    }*/

    /**
     * BELOW getCount and addAll method helps to call the addAll Method in MoviesFragmentGrid so that the settings and filter
     * is working. Without this the list will not be populated per sharedpreferences
      */

    /**
     * In getCount() method return size of your class variable which is arraylist
     *In getCount() method return size of your class variable which is arraylist
     * Yes you will have to override that method its part of ArrayAdapter class which you're extending
     *
     */

    /** @Override public int getCount() {
    if(mMovieList != null){
    return mMovieList.size(); }
    else {
    return 0;
    } }*
     */

/**Solution to  addAll method called in MoviesFramentGrid that does not call "notifyDataSetChanged()"
 * method which notifies adapter of changed data:
 * Make your own addAll method which takes arraylist of movies and sets
 * it to the adapter class variable which is also arraylist and in that method
 * you can call "notifyDataSetChanged()"
 *
 * one class variable for adapter class which will be your arraylist of movies
 * Now addAll() method in Movies Fragment class will perform same function as that of constructor but it will also
 * call notifyDataSetChanged() which will notify adapter of data changed
 */
    /** public void addAll(List<Movie> movie){
     this.mMovieList = movie;
      notifyDataSetChanged();
   }*/

    /**
     * This method returns Poster URL (string) from the passed cursor.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_movie_poster_path = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        Log.d(LOG_TAG, "Column Index: " + idx_movie_poster_path);
        return cursor.getString(idx_movie_poster_path);
    }

}