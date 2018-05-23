package com.arcsoft.sdk_demo;

public class User {
    String name;
    String type;
    String allowdate;
    String addtime;
    String uid;

    public User(String uid,String name, String type, String allowdate, String addtime) {
        this.uid=uid;
        this.name = name;
        this.type = type;
        this.allowdate = allowdate;
        this.addtime = addtime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAllowdate() {
        return allowdate;
    }

    public void setAllowdate(String allowdate) {
        this.allowdate = allowdate;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
