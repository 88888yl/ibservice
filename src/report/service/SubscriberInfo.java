package report.service;

import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/3.
 */
public class SubscriberInfo {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public SubscriberInfo(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public List<List<Object>> getInfoList(String dayOfWeek) {
        getConnect();

        List<List<Object>> subscriberList = new ArrayList<List<Object>>();

        String result_sql = "select * from \"Users of Report Service\"";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(result_sql);
            while (rs.next()) {
                if (dayOfWeek.equals(rs.getString("Day of Week"))) {
                    List<Object> subscriberInfo = new ArrayList<Object>();
                    subscriberInfo.add(rs.getString("Email"));
                    subscriberInfo.add(rs.getString("Type"));
                    subscriberInfo.add(rs.getString("Start Time"));
                    subscriberInfo.add(rs.getString("Day of Week"));

                    String[] itemArray = rs.getString("Products").substring(1, rs.getString("Products").length() - 1).split(", ");
                    subscriberInfo.add(itemArray);

                    subscriberList.add(subscriberInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }

        return subscriberList;
    }

    private void getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void closeAll() {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (con != null)
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
