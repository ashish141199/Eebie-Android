package com.eebie.eebie;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.eebie.eebie.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public final class Methods {
    public static User getRealmUserInstance(Context context) {
        Realm.init(context);

        RealmConfiguration config = new RealmConfiguration.Builder().name("users.realm").build();
        Realm realm = Realm.getInstance(config);
        User u = realm.where(User.class).findFirst();
        return u;
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static User getUserInstance(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String currentUserJson = prefs.getString("currentUser", null);
        JSONObject j=null;

        try {
            j = new JSONObject(currentUserJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User currentUser = gson.fromJson(j.toString(), User.class);
        return currentUser;
    }


}
