package com.randomringtonemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RingtoneHelper {
    private static int TotalCount = 0;
    private static int count;
    public static String title_tone = "";

    public static List<Ringtone> fetchAvailableRingtones(Context context) {

        List<Ringtone> ringtones = new ArrayList<>();
        RingtoneManager mgr = new RingtoneManager(context);
        mgr.setType(RingtoneManager.TYPE_RINGTONE);
        int n = mgr.getCursor().getCount();
        for (int i = 0; i < n; i++) {
            if (mgr.getRingtoneUri(i).getPath().contains("external")) {
                ringtones.add(mgr.getRingtone(i));
                System.out.println(" " + i + " Ringtone : " + mgr.getRingtone(i).getTitle(context) + " Ringtone : " + mgr.getRingtoneUri(i));
            }
        }
        return ringtones;
    }

    public static void changeRingtone(Context context) {

        SharedPreferences preferences = context.getSharedPreferences("randomizer", Context.MODE_PRIVATE);
        if (!preferences.getBoolean("active", false))
            return;
        List<Uri> ringtones = new ArrayList<>();
        List<String> titile = new ArrayList<>();
        RingtoneManager mgr = new RingtoneManager(context);
        mgr.setType(RingtoneManager.TYPE_RINGTONE);
        int n = mgr.getCursor().getCount();
        for (int i = 0; i < n; i++) {
            if (mgr.getRingtoneUri(i).getPath().contains("external")) {
                ringtones.add(mgr.getRingtoneUri(i));
                titile.add(mgr.getRingtone(i).getTitle(context));
            }
        }
        Random random = new Random(System.currentTimeMillis());
        int count = random.nextInt(ringtones.size());
//        System.out.println("Value of n "+ ringtones.size());
//        if (ringtones.size()-1 <= count)
//            count = 0;
//        else
//            count++;

        RingtoneManager.setActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_RINGTONE, ringtones.get(count));
        title_tone = titile.get(count);
        Session.saveSession("Count", String.valueOf(count), context);
        Session.saveSession("Title", titile.get(count), context);
        System.out.println("Count check Helper class " + Session.getSession("Count", context) + "   //   " + Session.getSession("Title", context));
        MainActivity.change_tone_txt();
        Log.d("media", "Current Default Ringtone : " + titile.get(count) + " Count value   " + count);

    }

    public static void set_count(int c) {
        count = c;
    }
}
