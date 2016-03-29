package com.codepath.apps.restclienttemplate.utils;

import android.text.format.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Asus on 3/29/2016.
 */
public class TweetUtil {

    public static boolean isConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int value = process.waitFor();
            return (value == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String setTimeAgo(String date) {
        String timeFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
        dateFormat.setLenient(true);
        String relateDate = "";


        try {
            long dateMs = dateFormat.parse(date).getTime();
            relateDate = DateUtils.getRelativeTimeSpanString(dateMs, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        relateDate = relateDate.replaceAll("hours ago", "h");
        relateDate = relateDate.replaceAll("hour ago", "h");
        relateDate = relateDate.replaceAll("minute ago", "h");
        relateDate = relateDate.replaceAll("seconds ago", "s");
        relateDate = relateDate.replaceAll("second ago", "s");
        relateDate = relateDate.replaceAll("0s", "just now");
        relateDate = relateDate.replaceAll("minutes ago", "h");
        return  relateDate;
    }

}
