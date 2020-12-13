package com.easylife.wishy;

public class Model {

    String name,upUrl,profileUrl,time;

    public Model(String name, String upUrl, String profileUrl,String time) {
        this.name = name;
        this.upUrl = upUrl;
        this.profileUrl = profileUrl;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpUrl() {
        return upUrl;
    }

    public void setUpUrl(String upUrl) {
        this.upUrl = upUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
