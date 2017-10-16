package tv.freetel.pmovies2.network.model;

/**
 * represents Movie list results from Open Movie DB API.
 * A POJO that represents Movie list from Open Movie DB API.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MovieInfo {

    private int mPage;

    @SerializedName("results")
    private List<Movie> mMovieList = new ArrayList<Movie>();

    @SerializedName("total_results")
    private int mTotalResults;

    @SerializedName("total_pages")
    private int mTotalPages;

    public MovieInfo(int mPage, List<Movie> mMovieList, int mTotalResults, int mTotalPages) {
        this.mPage = mPage;
        this.mMovieList = mMovieList;
        this.mTotalResults = mTotalResults;
        this.mTotalPages = mTotalPages;
    }

    public int getmPage() {
        return mPage;
    }

    public void setmPage(int mPage) {
        this.mPage = mPage;
    }

    public List<Movie> getmMovieList() {
        return mMovieList;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
    }

    public int getmTotalResults() {
        return mTotalResults;
    }

    public void setmTotalResults(int mTotalResults) {
        this.mTotalResults = mTotalResults;
    }

    public int getmTotalPages() {
        return mTotalPages;
    }

    public void setmTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }
}