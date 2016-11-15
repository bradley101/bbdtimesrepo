package com.tbt;

/**
 * Created by bradley on 31-08-2016.
 */
public class GloryNewsDetail {
    String gloryNews, status;
    GloryNewsDetail(String gloryNews, String status) {
        this.gloryNews = gloryNews;
        this.status = status;
    }

    public String getGloryNews() {
        return gloryNews;
    }

    public String getStatus() {
        return status;
    }
}
