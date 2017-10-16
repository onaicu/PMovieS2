package tv.freetel.pmovies2.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * represents MovieReview object.
 * A POJO that represents MovieReview object.
 */

public class MovieReview implements Parcelable {

    @SerializedName("id")
    private String mId;

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("content")
    private String mContent;

    @SerializedName("url")
    private String mUrl;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mAuthor);
        out.writeString(mContent);
        out.writeString(mUrl);
    }

    public MovieReview() {
    }

    /**
     * A private constructor that can only be used by the CREATOR field.
     * You have to read the data from the Parcel in the exact same order you wrote it.
     *
     * @param in used to retrieve the values that we originally wrote into the `Parcel`
     */
    private MovieReview(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();

    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<MovieReview> CREATOR
            = new Parcelable.Creator<MovieReview>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        // We just need to copy this and change the type to match our class.

        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
    }
    };
}

