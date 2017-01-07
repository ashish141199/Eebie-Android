package com.eebie.eebie.API;

import com.eebie.eebie.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ashish on 1/4/2017.
 */

public interface UserAPI {
    @POST("/api/users/create/")
    Call<User> createUser(@Body User user);
}
