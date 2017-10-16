package tv.freetel.pmovies2.event;

public class MovieReviewsEvent {

    private int mMovieId;

    public MovieReviewsEvent() {
    }

    public MovieReviewsEvent(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
