package com.tbt;

/**
 * Created by bradley on 21-09-2016.
 */
public class CommunityDetail {
    private String name, branch, college, gs, mob;
    public CommunityDetail(String name, String branch, String college, String gs, String mob) {
        this.branch = branch;
        this.college = college;
        this.name = name;
        this.gs = gs;
        this.mob = mob;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public String getCollege() {
        return college;
    }

    public String getGs() {
        return gs;
    }

    public String getMob() {
        return mob;
    }
}
