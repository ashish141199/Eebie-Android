package com.eebie.eebie;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String str = "{\"username\":[\"A user with that username already exists.\"]}";
        assertTrue(str.toLowerCase().contains("A user with that username already exists".toLowerCase()));
    }
}