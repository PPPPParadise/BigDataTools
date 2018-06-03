package com.essec.dudu.project.twitter;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by dudu on 17/10/27.
 */
public class UserDO implements Serializable {
    private long iduser;
    private int numFollowers;
    private int numFriends;
    private String createdAt;

    public long getIduser() {
        return iduser;
    }

    public void setIduser(long iduser) {
        this.iduser = iduser;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    public int getNumFriends() {
        return numFriends;
    }

    public void setNumFriends(int numFriends) {
        this.numFriends = numFriends;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString(){
        return "Iduser:"+iduser+" NumFollowers:"+numFollowers+" NumFriends:"+numFriends+" CreatedAt:"+createdAt;
    }
}
