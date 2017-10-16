package tv.freetel.pmovies2.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * represents MovieTrailer list results from Open Movie DB API.
 * A POJO that represents MovieTrailer list from Open Movie DB API.
 */

public class TrailerInfo {
    @SerializedName("id")
    private Integer mId;

    @SerializedName("results")
    private List<Trailer> mResults = new ArrayList<Trailer>();

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public List<Trailer> getmResults() {
        return mResults;
    }

    public void setmResults(List<Trailer> mResults) {
        this.mResults = mResults;
    }
}
