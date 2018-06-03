package com.essec.dudu.project.twitter;

/**
 * Created by dudu on 17/10/27.
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import com.essec.dudu.project.twitter.*;
public abstract class BaseDAO {
    protected final DBUtil db = DBUtil.getDBUtil();
    protected ResultSet rs;
    private static BaseDAO baseDAO;

    protected void destroy() {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            db.close();
        }
    }
}
