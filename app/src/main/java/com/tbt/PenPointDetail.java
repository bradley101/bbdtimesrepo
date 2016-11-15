package com.tbt;

/**
 * Created by bradley on 30-08-2016.
 */
public class PenPointDetail {
    String name, author, dated, status;
    String content;
    int imgID;

    PenPointDetail(String name, String content, String author, String dated, String status, int resId) {
        this.name = name;
        this.author = author;
        this.dated = dated;
        this.imgID = resId;
        this.content = content;
        this.status = status;
    }

    public int getImgID() {
        return imgID;
    }

    public String getAuthor() {
        return author;
    }

    public String getDated() {
        return dated;
    }

    public String getName() { return name; }

    public String getStatus() { return status; }

    public String getContent() { return content; }
}
