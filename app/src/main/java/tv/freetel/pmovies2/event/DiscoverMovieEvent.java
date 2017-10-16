package tv.freetel.pmovies2.event;

/**
 * This event class is used to represent an event which is triggered to fetch movies from Open Movie DB API.
 *
 */
public class DiscoverMovieEvent {
    private String mSortBy;

    public DiscoverMovieEvent(String sortBy) {
        mSortBy= sortBy;
    }

    public DiscoverMovieEvent() {
    }

    public String getmSortBy() {
        return mSortBy;
    }

    public void setmSortBy(String mSortBy) {
        this.mSortBy = mSortBy;
    }
}