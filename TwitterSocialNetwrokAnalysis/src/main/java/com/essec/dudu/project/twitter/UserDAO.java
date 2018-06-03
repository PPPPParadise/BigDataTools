package com.essec.dudu.project.twitter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dudu on 17/10/27.
 */
public class UserDAO extends BaseDAO{
    private static UserDAO UserDAO = null;
    public static synchronized UserDAO getInstance() {
        if (UserDAO == null) {
            UserDAO = new UserDAO();
        }
        return UserDAO;
    }

    // update
    public boolean update(UserDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        try {
            // check
            if (queryByIduser(String.valueOf(stu.getIduser())).isEmpty()) {
                return result;
            }
            // update
            String sql = "update user set numFollowers=?,numFriends=?,createdAt=? where iduser=?";
            String[] param = { stu.getNumFollowers()+"",stu.getNumFriends()+"",stu.getCreatedAt()+"",stu.getIduser()+""};
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
    public boolean delete(UserDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        String sql = "delete from user where iduser=?";
        String[] param = { stu.getIduser()+"" };
        int rowCount = db.executeUpdate(sql, param);
        if (rowCount == 1) {
            result = true;
        }
        destroy();
        return result;
    }

    // add
    public boolean add(UserDO stu) {
        boolean result = false;
        if (stu == null) {
            return result;
        }
        try {
            // check
            if (!queryByIduser(stu.getIduser()+"").isEmpty()) {
                return result;
            }
            // insert
            String sql = "insert into user(iduser,numFollowers,numFriends,createdAt) values(?,?,?,?)";
            String[] param = { stu.getIduser()+"",stu.getNumFollowers()+"",stu.getNumFriends()+"",stu.getCreatedAt()+""};
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

    // query by date
    public List<UserDO> queryByDate(String date) {
        List<UserDO> stus = new ArrayList<UserDO>();
        if (date.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from user where createdAt like ?";
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
    public List<UserDO> queryByIduser(String iduser) {
        List<UserDO> stus = new ArrayList<UserDO>();
        if (iduser.length() < 0) {
            return stus;
        }
        int i = 0;
        String sql = "select * from user where iduser = ?";
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

    private void buildList(ResultSet rs, List<UserDO> list, int i) throws SQLException {
        UserDO stu = new UserDO();
        stu.setIduser(rs.getLong("iduser"));
        stu.setNumFollowers(rs.getInt("numFollowers"));
        stu.setNumFriends(rs.getInt("numFriends"));
        stu.setCreatedAt(rs.getString("createdAt"));
        list.add(stu);
    }
}
