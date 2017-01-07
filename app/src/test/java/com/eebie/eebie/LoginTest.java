package com.eebie.eebie;

import android.content.Context;
import android.content.Intent;

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

    }
}
