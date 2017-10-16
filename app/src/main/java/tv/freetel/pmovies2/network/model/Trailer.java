package tv.freetel.pmovies2.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * represents Movie Trailer object.
 * A POJO that represents Movie Trailer object.
 */

public class Trailer implements Parcelable {
    @SerializedName("id")
    private String mId;

    @SerializedName("iso_639_1")
    private String mIso6391;

    @SerializedName("iso_3166_1")
    private String mIso31661;

    @SerializedName("key")
    private String mKey;

    @SerializedName("name")
    private String mName;

    @SerializedName("site")
    private String mSite;

    @SerializedName("size")
    private Integer mSize;

    @SerializedName("type")
    private String mType;


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmIso6391() {
        return mIso6391;
    }

    public void setmIso6391(String mIso6391) {
        this.mIso6391 = mIso6391;
    }

    public String getmIso31661() {
        return mIso31661;
    }

    public void setmIso31661(String mIso31661) {
        this.mIso31661 = mIso31661;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSite() {
        return mSite;
    }

    public void setmSite(String mSite) {
        this.mSite = mSite;
    }

    public Integer getmSize() {
        return mSize;
    }

    public void setmSize(Integer mSize) {
        this.mSize = mSize;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mIso6391);
        out.writeString(mIso31661);
        out.writeString(mKey);
        out.writeString(mName);
        out.writeString(mSite);
        out.writeInt(mSize);
        out.writeString(mType);
    }

    public Trailer() {
    }

    /**
     * A private constructor that can only be used by the CREATOR field.
     * You have to read the data from the Parcel in the exact same order you wrote it.
     *
     * @param in used to retrieve the values that we originally wrote into the `Parcel`
     */
    private Trailer(Parcel in) {
        mId = in.readString();
        mIso6391 = in.readString();
        mIso31661 = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
        mType = in.readString();
    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        // We just need to copy this and change the type to match our class.

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
