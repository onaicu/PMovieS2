package tv.freetel.pmovies2.network.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * represents MovieReview list results from Open Movie DB API.
 * A POJO that represents MovieReview list from Open Movie DB API.
 */

public class ReviewInfo {

    @SerializedName("id")
    private int mId;

    @SerializedName("page")
    private int mPage;

    @SerializedName("results")
    private List<MovieReview> mReviewList = new ArrayList<MovieReview>();

    @SerializedName("total_pages")
    private Integer mTotalPages;

    @SerializedName("total_results")
    private Integer mTotalResults;

    public ReviewInfo() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmPage() {
        return mPage;
    }

    public void setmPage(int mPage) {
        this.mPage = mPage;
    }

    public List<MovieReview> getmReviewList() {
        return mReviewList;
    }

    public void setmReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }

    public Integer getmTotalPages() {
        return mTotalPages;
    }

    public void setmTotalPages(Integer mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    public Integer getmTotalResults() {
        return mTotalResults;
    }

    public void setmTotalResults(Integer mTotalResults) {
        this.mTotalResults = mTotalResults;
    }
}