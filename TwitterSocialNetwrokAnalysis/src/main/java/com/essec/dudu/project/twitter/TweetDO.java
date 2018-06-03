package com.essec.dudu.project.twitter;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by dudu on 17/10/27.
 */
public class TweetDO implements Serializable {
    private long idTweet;
    private String text;
    private String date;
    private long iduser;

    public long getIdTweet() {
        return idTweet;
    }

    public void setIdTweet(long idTweet) {
        this.idTweet = idTweet;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getIduser() {
        return iduser;
    }

    public void setIduser(long iduser) {
        this.iduser = iduser;
    }

    @Override
    public String toString(){
        return "IdTweet:"+idTweet+" Text:"+text+" Date:"+date+" IdUser:"+iduser;
    }

}
