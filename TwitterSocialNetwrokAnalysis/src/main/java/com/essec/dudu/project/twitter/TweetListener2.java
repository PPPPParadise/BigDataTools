package com.essec.dudu.project.twitter;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import twitter4j.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;



/**
 * Created by IntelliJ IDEA.
 * User: cataldi
 * Date: 3-feb-2010
 * Time: 19.21.19
 * To change this template use File | Settings | File Templates.
 */
public class TweetListener2 implements StatusListener {

    private double numTotalTweets;
    private Connection conn;
    private HashSet<Long> users;
    private static Detector detector;
    private TweetDAO tweetDAO;
    private UserDAO userDAO;

    public TweetListener2(Connection conn1) {
        conn = conn1;
        numTotalTweets = 0.0;
        users = new HashSet<Long>();
        tweetDAO = TweetDAO.getInstance();
        userDAO = UserDAO.getInstance();

        try {
            System.out.println(TweetListener2.class.getResource("/").getFile());
            DetectorFactory.loadProfile("src/main/resource/profiles");
        } catch (Exception e) {
            System.out.println("Ciao");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }

    }


    public void onException(Exception ex) {
        ex.printStackTrace();
        System.exit(1);
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        try {
            System.out.println("----SOSPENSION---");
            Thread.sleep(3600000);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void onScrubGeo(long l, long l1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onStallWarning(StallWarning t) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }}

    public void onDeletionNotice(StatusDeletionNotice status) {
    }

    public void onStatus(Status status) {
        try {

            Thread.sleep(300);

            //System.out.println("");
            // JSONObject json = new JSONObject(status);
            String tweet = status.getText();
            tweet = tweet.replaceAll("\\,", " ");
            tweet = tweet.replaceAll("\"", " ");
            //tweet = tweet.replaceAll("#","\\#");
            //
            // System.out.println("CULO"+tweet);

            Date dateTweet = status.getCreatedAt();

            User user = status.getUser();
            long idtweet = status.getId();
            long iduser = user.getId();
            Date userCreatedAt = user.getCreatedAt();
            int numFollowers = user.getFollowersCount();
            int numFriends = user.getFriendsCount();


            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTweet1 = sdf.format(dateTweet);
            String userCreatedAt1 = sdf.format(userCreatedAt);

            TweetDO tweetDO = new TweetDO();
            tweetDO.setIdTweet(idtweet);
            tweetDO.setText(tweet);
            tweetDO.setDate(dateTweet1);
            tweetDO.setIduser(iduser);

            UserDO userDO = new UserDO();
            userDO.setIduser(iduser);
            userDO.setNumFollowers(numFollowers);
            userDO.setNumFriends(numFriends);
            userDO.setCreatedAt(userCreatedAt1);

            detector = DetectorFactory.create();
            detector.append(tweet);
            String lang = detector.detect();

            if (lang.equals("en")) {
                tweetDAO.add(tweetDO);
                if (!users.contains(iduser)) {

                    userDAO.add(userDO);
                    users.add(iduser);
                }
                numTotalTweets++;
                if (numTotalTweets % 500 == 0) {
                    System.out.println("Numero Tweet: " + numTotalTweets);
                }
                if (numTotalTweets == 1000000) {
                    System.exit(0);
                }
            }
            System.out.println("onStatus got: " + status);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
