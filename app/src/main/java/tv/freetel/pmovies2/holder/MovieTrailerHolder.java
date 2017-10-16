package tv.freetel.pmovies2.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tv.freetel.pmovies2.R;
import tv.freetel.pmovies2.util.Constants;


public class MovieTrailerHolder extends RecyclerView.ViewHolder {

    private static final String LOG_TAG = MovieTrailerHolder.class.getSimpleName();
    private Context mContext;
    private TextView mVideoTitle;
    private TextView mVideoKey;
    private ImageView mPlay;

    public MovieTrailerHolder(View view, Context context) {
        super(view);

        this.mVideoTitle = (TextView) view.findViewById(R.id.trailer_item_textView);
        this.mVideoKey = (TextView) view.findViewById(R.id.trailer_video_key);
        this.mPlay = (ImageView) view.findViewById(R.id.imagePlay);
        this.mContext = context;


        //use an Intent to open a youtube link in either the native app or a web browser of choice.

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Play icon clicked");
                String youTubeUrl = Constants.YOU_TUBE_BASE_URL + mVideoKey.getText();
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUrl)));
            }
        });
    }

    public TextView getmVideoTitle() {
        return mVideoTitle;
    }

    public void setmVideoTitle(TextView mVideoTitle) {
        this.mVideoTitle = mVideoTitle;
    }

    public TextView getmVideoKey() {
        return mVideoKey;
    }

    public void setmVideoKey(TextView mVideoKey) {
        this.mVideoKey = mVideoKey;
    }

}

