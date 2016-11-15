package com.tbt;

/**
 * Created by bradley on 08-09-2016.
 */
public class FormsDetail {
    private String name, url, visibilityStatus, currentStatus;
    FormsDetail(String name, String url, String visibilityStatus, String currentStatus) {
        this.name = name;
        this.url = url;
        this.visibilityStatus = visibilityStatus;
        this.currentStatus = currentStatus;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getVisibilityStatus() {
        return visibilityStatus;
    }
}
