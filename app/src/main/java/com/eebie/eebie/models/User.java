package com.eebie.eebie.models;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

public class User extends RealmObject {

    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String dp;

    public User() {
    }

    public User(String username, String phone, String email) {
        this.username = username;
        this.phone = phone;
        this.email = email;
    }

    public User(User u) {
        this.username = u.username;
        this.phone = u.phone;
        this.email = u.email;
        this.id = u.id;
        this.dp = u.dp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }




    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
