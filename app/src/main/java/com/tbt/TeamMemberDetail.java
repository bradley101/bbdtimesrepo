package com.tbt;

/**
 * Created by bradley on 01-09-2016.
 */
public class TeamMemberDetail {
    String memberName, year, branch;
    int imgResId;
    TeamMemberDetail(String memberName, String year, String branch, int imgResId) {
        this.memberName = memberName;
        this.year = year;
        this.branch = branch;
        this.imgResId = imgResId;
    }

    public int getImgResId() {
        return imgResId;
    }

    public String getBranch() {
        return branch;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getYear() {
        return year;
    }
}
