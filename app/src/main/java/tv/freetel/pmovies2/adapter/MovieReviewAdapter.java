package tv.freetel.pmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.holder.MovieReviewHolder;
import tv.freetel.pmovies2.network.model.MovieReview;

/**
 * This class acts as a bridge between movie grid (a subclass of AdapterView)
 * and the underlying data for the movie Grid. The Adapter provides access to the data items.
 * This Adapter is also responsible for making a View for each item in the data set.
 *
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewHolder> {

    private Context mContext;
    private List<MovieReview> mMovieReviewList;

    public MovieReviewAdapter(List<MovieReview> reviews) {
        mMovieReviewList = reviews;
    }


    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new MovieReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieReviewHolder holder, int position) {
        MovieReview movieReview = mMovieReviewList.get(position);
        holder.getmAuthor().setText(movieReview.getmAuthor());
        holder.getmContent().setText(movieReview.getmContent());
    }

    @Override
    public int getItemCount() {

        if (mMovieReviewList == null)
            return 0;
        else {
            return mMovieReviewList.size();
        }
    }

    public List<MovieReview> getmMovieReviewList() {
        return mMovieReviewList;
    }

    public void setmMovieReviewList(List<MovieReview> mMovieReviewList) {
        this.mMovieReviewList = mMovieReviewList;
    }

    // create the add all function to pass info through adapter to the details screen fragment for the getReviews function.

    public void addAll(List<MovieReview> movieReviews) {
        this.mMovieReviewList = movieReviews;
        notifyDataSetChanged();
    }
}