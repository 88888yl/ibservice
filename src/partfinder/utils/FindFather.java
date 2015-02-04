package partfinder.utils;

import partfinder.database.DBtoObject;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-10-8.
 */
public class FindFather {
    private String URL;
    private String USER;
    private String PWD;
    private String tableName;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    List<DBtoObject> objectList = new ArrayList<DBtoObject>();

    public FindFather(String URL, String USER, String PWD, String tableName) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public Connection getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void insertFatherNumber() {
        try {
            StringBuffer sql = new StringBuffer();

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
//            rs = stmt.executeQuery("select \"Row Number\",\"Level\" from " + tableName + " order by \"Row Number\"");
            rs = stmt.executeQuery("select \"Row_Number\",\"Level_Number\" from " + tableName + " order by \"Row_Number\"");
            rs.next();

            while (rs.next()) {
                DBtoObject dbToObject = new DBtoObject();
//                dbToObject.setRow_number(rs.getInt("Row Number"));
                dbToObject.setRow_number(rs.getInt("Row_Number"));
//                dbToObject.setLevel_Number(rs.getInt("Level"));
                dbToObject.setLevel_Number(rs.getInt("Level_Number"));

                objectList.add(dbToObject);
            }

            objectList.get(0).setFather_number(0);
            for (int i = 1; i < objectList.size(); ++i) {
                int cur_level = objectList.get(i).getLevel_Number();
                int cur_row = objectList.get(i).getRow_number();
                int fa_level = cur_level - 1;
                int fa_row = 0;
                int previous = cur_row - 1;
                while (previous >= 0) {
                    if (objectList.get(previous).getLevel_Number() == fa_level) {
                        fa_row = objectList.get(previous).getRow_number();
                        break;
                    }
                    previous--;
                }
                objectList.get(i).setFather_number(fa_row);
            }

            conn.setAutoCommit(false);
            sql.append("update ").append(tableName).append(" set \"Father_Number\"=? where \"Row_Number\"=?");
//            sql.append("update ").append(tableName).append(" set \"Father Number\"=? where \"Row Number\"=?");
            ps = conn.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            for (int i = 0; i < objectList.size(); ++i) {
                ps.setInt(1, objectList.get(i).getFather_number());
                ps.setInt(2, objectList.get(i).getRow_number());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            System.out.println("Find father success!");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeAll() {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (ps != null)
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

//    public static void main(String[] args) {
//        FindFather findFather = new FindFather("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "myl", "815118", "T_5480614_3");
//        findFather.getConnect();
//        findFather.insertFatherNumber();
//        findFather.closeAll();
//    }
}
