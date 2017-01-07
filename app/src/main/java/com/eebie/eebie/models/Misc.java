package com.eebie.eebie.models;

import io.realm.RealmObject;

public class Misc extends RealmObject {
//    if "1" => user is logged in, else not logged in!
    private Integer log;

    public Integer getLog() {
        return log;
    }

    public void setLog(Integer log) {
        this.log = log;
    }
}
