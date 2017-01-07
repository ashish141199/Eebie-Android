package com.eebie.eebie;

import android.content.Context;

import com.eebie.eebie.models.Misc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
    @Mock
    Context mMockContext;
    @Test
    public void checkIfRealmIsWorking() {
        Realm.init(mMockContext.getApplicationContext());


        RealmConfiguration config = new RealmConfiguration.Builder().name("misc.realm").build();
        Realm realm = Realm.getInstance(config);
        //first creating a misc object with as log = 1 i.e indicating that the current user is logged in
        Misc m = realm.createObject(Misc.class);
        m.setLog(1);
        realm.commitTransaction();
        //now the user has once logged in. So the user is currently logged in and the user's trying to open the app again.
        Misc m1 = realm.where(Misc.class).findFirst();
        assertTrue("Will return True only when the user is logged in (log=1)", m1.getLog() == 1);

        realm.close();


    }

}
