package com.eebie.eebie.models;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

public class User extends RealmObject {

    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private Integer points;
    private Integer energy;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User() {
    }

    public User(Integer id, String username, String email, String phone, String fullName, Integer energy, Integer points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.energy = energy;
        this.points = points;
    }

    public User(String username) {
        this.username = username;

    }

    public User(User u) {
        this.username = u.username;
        this.phone = u.phone;
        this.email = u.email;
        this.id = u.id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
