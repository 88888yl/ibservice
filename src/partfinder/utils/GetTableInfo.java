package partfinder.utils;

import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-10-17.
 */
public class GetTableInfo {
    private String URL = GlobalVariables.oracleUrl;
    private String USER = GlobalVariables.oracleUserName;
    private String PWD = GlobalVariables.oraclePassword;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public GetTableInfo() {
    }

    public GetTableInfo(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public Connection getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("Select table_name From User_Tables WHERE table_name like 'BOM%'");
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    public List<String> getTableDesc() {
        List<String> tableDescs = new ArrayList<String>();
        List<String> tableNames = new ArrayList<String>();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("Select table_name From User_Tables WHERE table_name like 'I%'");
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tableNames.size(); i++) {
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery("Select \"Description\" From " + tableNames.get(i));
                while (rs.next()) {
                    tableDescs.add(rs.getString("Description"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return tableDescs;
    }

    public void closeAll() {
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
}
