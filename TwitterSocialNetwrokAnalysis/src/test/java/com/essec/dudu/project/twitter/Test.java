package com.essec.dudu.project.twitter;

/**
 * Created by dudu on 17/10/28.
 */
public class Test {
    public static void main(String[] args){
        TweetDO tweetDO = new TweetDO();
        tweetDO.setIduser(1);
        tweetDO.setDate("2017-09-09");
        tweetDO.setText("Paris");
        tweetDO.setIdTweet(2);

        TweetDAO tweetDAO = TweetDAO.getInstance();
        tweetDAO.add(tweetDO);
        tweetDO.setText("BeiJing");
        tweetDAO.update(tweetDO);
        System.out.println(tweetDAO.queryById("2"));
        System.out.println(tweetDAO.queryByIduser("1"));
        System.out.println(tweetDAO.queryByDate("2017-09-09"));
        System.out.println(tweetDAO.queryByText("Bei"));
        tweetDAO.delete(tweetDO);

        UserDO userDO = new UserDO();
        userDO.setIduser(1);
        userDO.setNumFollowers(2);
        userDO.setNumFriends(3);
        userDO.setCreatedAt("2017-09-09");

        UserDAO userDAO = UserDAO.getInstance();
        userDAO.add(userDO);
        userDO.setNumFollowers(111);
        userDAO.update(userDO);
        System.out.println(userDAO.queryByIduser("1"));
        System.out.println(userDAO.queryByDate("2017-09-09"));
        userDAO.delete(userDO);

    }
}
