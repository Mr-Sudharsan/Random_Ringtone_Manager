package com.randomringtonemanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session {

    public static void saveSession(String key, int value, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static int getSession(String key, Context context,int a) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            return prefs.getInt(key,0);
        } else {
            return 0;
        }
    }

    public static void putListUri(String key, List<Uri> uriList, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            Uri[] myUriList = uriList.toArray(new Uri[uriList.size()]);
            editor.putString(key, TextUtils.join("‚_‚", myUriList)).apply();
        }
    }

    public static List<Uri> getListUri(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        String[] myList = TextUtils.split(prefs.getString(key, ""), "‚_‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        List<Uri> newList = new ArrayList<Uri>();
        for (String item : arrayToList)
            newList.add(Uri.parse(item));
        return newList;
    }

    public static void saveSession(String key, String value, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static void saveSessionInt(String key, Integer value, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static void saveSession(String key, Long value, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static void clearAllSession(Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            prefs.getAll().clear();
            return;
        } else {
            return;
        }
    }

    public static String getSession(String key, Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            return prefs.getString(key, "");
        } else {
            return "";
        }
    }

    public static Integer getSessionInt(String key, Context context) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            return prefs.getInt(key, -1);
        } else {
            return -1;
        }
    }

    public static void saveSession(String key, boolean value, Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static boolean getSession(String key, Context context, boolean a) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            return prefs.getBoolean(key, false);
        } else {
            return false;
        }
    }

    public static long getSession(String key, Context context, long a) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
            return prefs.getLong(key, 0);
        } else {
            return 0;
        }
    }
}
