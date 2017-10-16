package tv.freetel.pmovies2.event;

import java.util.List;

import tv.freetel.pmovies2.network.model.Movie;

/**
 * Ths event class is used to represent response returned by discover endpoint of  Open Movie DB API.
 *
 */
public class MovieEvent {

    List<Movie> mMovieList;

    public MovieEvent() {
    }

    public MovieEvent(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
    }

    public List<Movie> getmMovieList() {
        return mMovieList;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
    }
}
