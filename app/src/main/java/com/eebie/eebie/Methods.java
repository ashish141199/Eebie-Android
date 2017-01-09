package com.eebie.eebie;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.eebie.eebie.models.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public final class Methods {
    public static User getUserInstance(Context context) {
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

}
