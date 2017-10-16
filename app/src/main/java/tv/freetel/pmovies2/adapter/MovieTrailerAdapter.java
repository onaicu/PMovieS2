package tv.freetel.pmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.holder.MovieTrailerHolder;
import tv.freetel.pmovies2.network.model.Trailer;

/**
 * This class acts as a bridge between movie grid (a subclass of AdapterView)
 * and the underlying data for the movie Grid. The Adapter provides access to the data items.
 * This Adapter is also responsible for making a View for each item in the data set.
 *
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerHolder> {

    private Context mContext;
    private List<Trailer> mMovieTrailerList;

    public MovieTrailerAdapter(List<Trailer> trailers, Context context) {
        mMovieTrailerList = trailers;
        //take context variable in your MovieTrailerAdapter constructor and then you'll have to assign it mContext variable
        this.mContext = context;
    }


    @Override
    public MovieTrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new MovieTrailerHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(MovieTrailerHolder holder, int position) {
        Trailer movieTrailer = mMovieTrailerList.get(position);
        holder.getmVideoTitle().setText(movieTrailer.getmName());
        holder.getmVideoKey().setText(movieTrailer.getmKey());
    }

    @Override
    public int getItemCount() {
        if (mMovieTrailerList == null) {
            return 0;
        } else
            return mMovieTrailerList.size();
    }

    public List<Trailer> getmMovieTrailerList() {
        return mMovieTrailerList;
    }

    public void setmMovieTrailerList(List<Trailer> mMovieTrailerList) {
        this.mMovieTrailerList = mMovieTrailerList;
    }
    // create the add all function to pass info through adapter to the details screen fragment for the getReviews function.

    public void addAll(List<Trailer> movieTrailers) {
        this.mMovieTrailerList = movieTrailers;
        notifyDataSetChanged();
    }
}