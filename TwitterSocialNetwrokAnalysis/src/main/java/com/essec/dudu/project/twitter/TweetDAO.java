package com.essec.dudu.project.twitter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dudu on 17/10/27.
 */
public class TweetDAO extends BaseDAO {
    private static TweetDAO tweetDAO = null;
    public static synchronized TweetDAO getInstance() {
        if (tweetDAO == null) {
            tweetDAO = new TweetDAO();
        }
        return tweetDAO;
    }

    // update
    public boolean update(TweetDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        try {
            // check
            if (queryById(stu.getIdTweet()+"").isEmpty()) {
                return result;
            }
            // update
            String sql = "update tweet set text=?,date=?,iduser=? where idTweet=?";
            String[] param = { stu.getText(),stu.getDate(),stu.getIduser()+"", stu.getIdTweet()+""};
            int rowCount = db.executeUpdate(sql, param);
            if (rowCount == 1) {
                result = true;
            }
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return result;
    }

    // delete
    public boolean delete(TweetDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        String sql = "delete from tweet where idTweet=?";
        String[] param = {stu.getIdTweet()+"" };
        int rowCount = db.executeUpdate(sql, param);
        if (rowCount == 1) {
            result = true;
        }
        destroy();
        return result;
    }

    // add
    public boolean add(TweetDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        try {
            // check
            if (!queryById(stu.getIdTweet()+"").isEmpty()) {
                return result;
            }
            // insert
            String sql = "insert into tweet(idTweet,text,date,iduser) values(?,?,?,?)";
            String[] param = {stu.getIdTweet()+"",stu.getText()+"",stu.getDate()+"",stu.getIduser()+""};
            if (db.executeUpdate(sql, param) == 1) {
                result = true;
            }
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return result;
    }

    // query by idTweet
    public List<TweetDO> queryById(String idTweet) {
        List<TweetDO> stus = new ArrayList<TweetDO>();
        if (idTweet.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from tweet where idTweet = ?";
        String[] param = { idTweet };
        rs = db.executeQuery(sql, param);
        try {
            while (rs.next()) {
                buildList(rs, stus, i);
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return stus;
    }

    // query by date
    public List<TweetDO> queryByDate(String date) {
        List<TweetDO> stus = new ArrayList<TweetDO>();
        if (date.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from tweet where date like ?";
        String[] param = { date+"%" };
        rs = db.executeQuery(sql, param);
        try {
            while (rs.next()) {
                buildList(rs, stus, i);
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return stus;
    }

    // query by iduser
    public List<TweetDO> queryByIduser(String iduser) {
        List<TweetDO> stus = new ArrayList<TweetDO>();
        if (iduser.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from tweet where iduser = ?";
        String[] param = { iduser };
        rs = db.executeQuery(sql, param);
        try {
            while (rs.next()) {
                buildList(rs, stus, i);
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return stus;
    }

    // query by text
    public List<TweetDO> queryByText(String text) {
        List<TweetDO> stus = new ArrayList<TweetDO>();
        if (text.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from tweet where text like ?";
        String[] param = { "%"+text+"%" };
        rs = db.executeQuery(sql, param);
        try {
            while (rs.next()) {
                buildList(rs, stus, i);
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return stus;
    }

    // query by date and text
    public List<TweetDO> queryByDateAndText(String date,String text) {
        List<TweetDO> stus = new ArrayList<TweetDO>();
        if (text.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from tweet where date like ? and text like ?";
        String[] param = { date+"%","%"+text+"%" };
        rs = db.executeQuery(sql, param);
        try {
            while (rs.next()) {
                buildList(rs, stus, i);
                i++;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            destroy();
        }
        return stus;
    }

    private void buildList(ResultSet rs, List<TweetDO> list, int i) throws SQLException {
        TweetDO stu = new TweetDO();
        stu.setIdTweet(rs.getLong("idTweet"));
        stu.setText(rs.getString("text"));
        stu.setDate(rs.getString("date"));
        stu.setIduser(rs.getLong("iduser"));
        list.add(stu);
    }
}
