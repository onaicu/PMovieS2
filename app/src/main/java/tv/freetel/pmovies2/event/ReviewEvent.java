package tv.freetel.pmovies2.event;

import java.util.ArrayList;
import java.util.List;

import tv.freetel.pmovies2.network.model.MovieReview;


public class ReviewEvent {

    private List<MovieReview> mReviewList = new ArrayList<MovieReview>();

    public ReviewEvent(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }

    public List<MovieReview> getmReviewList() {
        return mReviewList;
    }

    public void setmReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }
}
