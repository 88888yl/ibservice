package report.database;

import utils.GlobalVariables;

import java.sql.*;
import java.util.Arrays;

/**
 * Created by myl
 * on 2015/2/3.
 */
public class ReportServiceDB {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private String serviceType;
    private String startTime;
    private String dayOfWeek;
    private String email;
    private String[] itemArray;

    public ReportServiceDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createReportDB() {
        getConnect();
        deleteReportTable();
        String create_sql = "create table \"Users of Report Service\" (" +
                "\"Email\" varchar2(30)," +
                "\"Type\" varchar2(30)," +
                "\"Start Time\" varchar2(30)," +
                "\"Day of Week\" varchar2(30)," +
                "\"Products\" varchar2(4000))";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql);
            stmt.close();
            System.out.println();
            System.out.println("Users of Report Service Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    public void insertReportDB() {
        if(!validateTableExist()){
            createReportDB();
        }
        getConnect();

        String inset_sql = "insert into \"Users of Report Service\" (" +
                "\"Email\"," +
                "\"Type\"," +
                "\"Start Time\"," +
                "\"Day of Week\"," +
                "\"Products\"" +
                ") VALUES (" +
                "\'" + getEmail() + "\'," +
                "\'" + getServiceType() + "\'," +
                "\'" + getStartTime() + "\'," +
                "\'" + getDayOfWeek() + "\'," +
                "\'" + Arrays.toString(getItemArray()) + "\'" +
                ")";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(inset_sql);
            rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private boolean validateTableExist() {
        getConnect();
        String tableName = "Users of Report Service";
        int result = 0;
        String sql = "select table_name from user_tables";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (tableName.equals(rs.getString("TABLE_NAME"))) {
                    result = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
        return result != 0;
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

    private void deleteReportTable() {
        String delete_sql = "drop table \"Users of Report Service\"";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(delete_sql);
            stmt.close();
        } catch (SQLException ignored) {
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getItemArray() {
        return itemArray;
    }

    public void setItemArray(String[] itemArray) {
        this.itemArray = itemArray;
    }
}
