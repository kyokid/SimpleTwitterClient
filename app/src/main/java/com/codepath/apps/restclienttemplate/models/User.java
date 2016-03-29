package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Asus on 3/28/2016.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable {

    @Column(name = "name")
    String mName;

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long mId;

    @Column(name = "screen_name")
    String mScreenName;

    @Column(name = "image_url")
    String mImageUrl;

    public User() {
        super();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeLong(this.mId);
        dest.writeString(this.mScreenName);
        dest.writeString(this.mImageUrl);
    }

    protected User(Parcel in) {
        this.mName = in.readString();
        this.mId = in.readLong();
        this.mScreenName = in.readString();
        this.mImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public List<Tweet> tweets() {
        return getMany(Tweet.class, "Users");
    }

    public static User fromJson(JSONObject object) {
        User user = new User();
        try {
            user.mScreenName = object.getString("name");
            user.mImageUrl = object.getString("profile_image_url");
            user.mScreenName = object.getString("screen_name");
            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User findOrCreateFromJson(JSONObject object) {
        long remoteId = 0;
        try {
            remoteId = object.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User existed = new Select().from(User.class).where("remote_id = ?", remoteId).executeSingle();
        if (existed != null) {
            return existed;
        } else {
            User user = User.fromJson(object);
            user.save();
            return user;
        }
    }

    public String getmName() {
        return mName;
    }

    public long getmId() {
        return mId;
    }

    public String getmScreenName() {
        return mScreenName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}
