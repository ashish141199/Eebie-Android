package com.eebie.eebie;

import android.content.Context;

import com.eebie.eebie.models.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ashish on 1/6/2017.
 */

public class Methods {
    public static User getUserInstance(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().name("users.realm").build();
        Realm realm = Realm.getInstance(config);
        User u = realm.where(User.class).findFirst();
        return u;
    }
}
