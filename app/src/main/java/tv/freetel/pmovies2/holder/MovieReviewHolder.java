package tv.freetel.pmovies2.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import tv.freetel.pmovies2.R;

public class MovieReviewHolder extends RecyclerView.ViewHolder {

    private TextView mAuthor;
    private TextView mContent;

    public MovieReviewHolder(View view) {
        super(view);
        this.mAuthor = (TextView) view.findViewById(R.id.review_item_author);
        this.mContent = (TextView) view.findViewById(R.id.review_item_content);
    }

    public TextView getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(TextView mAuthor) {
        this.mAuthor = mAuthor;
    }

    public TextView getmContent() {
        return mContent;
    }

    public void setmContent(TextView mContent) {
        this.mContent = mContent;
    }
}
