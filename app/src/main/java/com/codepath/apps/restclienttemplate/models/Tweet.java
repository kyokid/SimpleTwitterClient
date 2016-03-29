package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Asus on 3/28/2016.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {

    @Column(name = "body_url")
    String mBodyUrl;

    @Column(name = "body")
    String mBody;

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long mId;

    @Column(name = "created_at")
    String mCreateAt;

    @Column(name = "image_url")
    String mImageUrl;

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    User mUser;

    @Column(name = "retweet_count")
    long mRetweetCount;

    @Column(name = "favorite_count")
    long mFavorite;




    public Tweet() {
        super();
    }

    public static Tweet fromJson(JSONObject object) {
        Tweet tweet = new Tweet();
        try {
            tweet.mBody = object.getString("text");
            tweet.mCreateAt = object.getString("created_at");
            tweet.mUser = User.findOrCreateFromJson(object.getJSONObject("user"));
            if (object.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("type").equals("photo")) {
                tweet.mImageUrl = object.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            } else {
                tweet.mImageUrl = null;
            }
            tweet.mBodyUrl = object.getJSONObject("entities").getJSONArray("urls").getJSONObject(0).getString("display_url");
            tweet.mBody = clearUrl(tweet.mBodyUrl);
            tweet.mRetweetCount = object.getLong("retweet_count");
            tweet.mFavorite = object.getLong("favorite_count");
            if (tweet.mBodyUrl.length() > 0 ) {
                tweet.mBody += tweet.mBodyUrl;
            }
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static String clearUrl(String mBodyUrl) {
        return mBodyUrl.replaceAll("http:\\//t.co\\/\\w*", "");
    }


    public static ArrayList<Tweet> fromJsonArray(JSONArray array) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                Tweet tweet = Tweet.findOrCreateFromJson(object);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }



    public static Tweet findOrCreateFromJson(JSONObject object) {
        long remoteId = 0;
        try {
            remoteId = object.getLong("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Tweet existed = new Select().from(Tweet.class).where("remote_id = ?", remoteId).executeSingle();
        if (existed != null) {
            return existed;
        } else {
            Tweet tweet = Tweet.fromJson(object);
            tweet.save();
            return tweet;
        }
    }





    public String getmBody() {
        return mBody;
    }

    public long getmId() {
        return mId;
    }

    public String getmCreateAt() {
        return mCreateAt;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public User getmUser() {
        return mUser;
    }

    public String getmBodyUrl() {
        return mBodyUrl;
    }

    public long getmRetweetCount() {
        return mRetweetCount;
    }

    public long getmFavorite() {
        return mFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBodyUrl);
        dest.writeString(this.mBody);
        dest.writeLong(this.mId);
        dest.writeString(this.mCreateAt);
        dest.writeString(this.mImageUrl);
        dest.writeParcelable(this.mUser, flags);
        dest.writeLong(this.mRetweetCount);
        dest.writeLong(this.mFavorite);
    }

    protected Tweet(Parcel in) {
        this.mBodyUrl = in.readString();
        this.mBody = in.readString();
        this.mId = in.readLong();
        this.mCreateAt = in.readString();
        this.mImageUrl = in.readString();
        this.mUser = in.readParcelable(User.class.getClassLoader());
        this.mRetweetCount = in.readLong();
        this.mFavorite = in.readLong();
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
