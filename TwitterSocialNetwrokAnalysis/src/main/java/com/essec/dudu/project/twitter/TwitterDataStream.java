package com.essec.dudu.project.twitter;

import twitter4j.*;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.io.IOException;
import java.lang.System;
import java.sql.*;

/**
 * Created by dudu on 17/10/26.
 */
public class TwitterDataStream {
    static String accessToken = "109226669-77QGoI2Cqoir71GWJKRlq8RJEAqRC628FARdJ897";
    static String accessTokenSecret = "0I6DRWcmbz2oF76Socmg6OedRUoar6u0FcD9MtJaceK0j";
    static String consumerKey = "basXIvROD3J1BSAvWOWwxm2Wi";
    static String consumerSecret = "C1mApGdB2ZDbQzNZ7Sfsj4N6vX5CSPBXGtqAsLh6Q55TlQA0CE";
    static String conn_str = "jdbc:mysql://localhost/twitter?useUnicode=yes&characterEncoding=UTF8&user=root&password=root";

    static String sql_for_tweet = "create table tweet (idTweet bigint,text text,date date,iduser bigint);";
    static String sql_for_user = "create table user (iduser bigint, numFollowers bigint, numFriends bigint, createdAt date);";

    public static void main(String[] args) {
        Connection conn = DBUtil.getDBUtil().getConn();
        try{
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            System.out.println("成功加载MySQL驱动程序");

            ResultSet result = DBUtil.getDBUtil().executeQuery("show tables;");
            if (result != null) {
                while (result.next()){
                    System.out.println(result.getString(1) + "\t");
                }
            }

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthAccessToken(accessToken);
            cb.setOAuthAccessTokenSecret(accessTokenSecret);
            cb.setOAuthConsumerKey(consumerKey);
            cb.setOAuthConsumerSecret(consumerSecret);
            Configuration cbConf= cb.build();
            OAuthAuthorization auth = new OAuthAuthorization(cbConf);
            TwitterStream twitterStream;
            twitterStream = new TwitterStreamFactory(cbConf).getInstance();
//            StatusListener listener = new StatusListener() {
//
//                public void onStatus(Status status) {
//                    //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
//                    //System.out.println(status);
//                    String str = DataObjectFactory.getRawJSON(status);
//                    try {
////                        //JSONObject nnstr = new JSONObject(newstr);
////                        DBObject dbObject =(DBObject)JSON.parse(str);
////                        pr.collection.insert(dbObject);
////                        System.out.println(dbObject);
////                        pr.count++;
////                        if(pr.count>300000) {
////                            pr.mongo.close();
////                            System.exit(0);
////                        }
//                        System.out.println(str);
//                    }  catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//                    System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
//                }
//
//
//                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//                    System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
//                }
//
//
//                public void onScrubGeo(long userId, long upToStatusId) {
//                    System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
//                }
//
//
//                public void onStallWarning(StallWarning warning) {
//                    System.out.println("Got stall warning:" + warning);
//                }
//
//
//                public void onException(Exception ex) {
//                    ex.printStackTrace();
//                }
//            };
            TweetListener2 list = new TweetListener2(conn);
            twitterStream.addListener(list);
            twitterStream.sample();
        }catch(SQLException e){
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.getDBUtil().close();
        }
    }
}

