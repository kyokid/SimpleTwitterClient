package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.TweetUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Asus on 3/29/2016.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private ArrayList<Tweet> mTweet;
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_profile) ImageView mProfileImg;
        @Bind(R.id.img_tweet)ImageView mTweetImg;
        @Bind(R.id.txt_screen_name)TextView mScreenName;
        @Bind(R.id.txt_username)TextView mUsername;
        @Bind(R.id.txt_body)TextView mBodyMessage;
        @Bind(R.id.txt_created_at)TextView mCreatedAt;
        @Bind(R.id.txt_reply) TextView mReply;
        @Bind(R.id.txt_reTweet) TextView mReTweet;
        @Bind(R.id.txt_favorite) TextView mFavorite;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TweetAdapter(ArrayList<Tweet> tweets) {
        mTweet = tweets;
    }

    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.tweet_item, parent, false);

        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position) {
        Tweet tweet = mTweet.get(position);
        holder.mBodyMessage.setText(tweet.getmBody());
        Linkify.addLinks(holder.mBodyMessage, Linkify.WEB_URLS);
        String createdAt = TweetUtil.setTimeAgo(tweet.getmCreateAt());
        holder.mCreatedAt.setText(createdAt);
        holder.mScreenName.setText("@" + tweet.getmUser().getmScreenName());
        holder.mUsername.setText(tweet.getmUser().getmName());
        holder.mReTweet.setText(String.valueOf(tweet.getmRetweetCount()));
        holder.mFavorite.setText(String.valueOf(tweet.getmFavorite()));
        final ImageView profileImg = holder.mProfileImg;
        Glide.with(mContext)
                .load(tweet.getmUser().getmImageUrl())
                .asBitmap().into(new BitmapImageViewTarget(profileImg){
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                        .create(mContext.getResources(), resource);
                roundedBitmapDrawable.setCornerRadius(6);
                profileImg.setImageDrawable(roundedBitmapDrawable);
            }
        });
        if (tweet.getmImageUrl() != null) {
            holder.mTweetImg.setVisibility(View.VISIBLE);
            final ImageView tweetImg = holder.mTweetImg;
            Glide.with(mContext)
                    .load(tweet.getmImageUrl())
                    .asBitmap().into(new BitmapImageViewTarget(tweetImg) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                            .create(mContext.getResources(), resource);
                    roundedBitmapDrawable.setCornerRadius(16);
                    tweetImg.setImageDrawable(roundedBitmapDrawable);
                }
            });
        } else {
            holder.mTweetImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTweet != null ? mTweet.size() : 0;
    }
}
