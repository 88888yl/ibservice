package cso.database;

import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-12-12.
 */
public class EmailToDB {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public EmailToDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createAddrTable() {
        String create_sql = "create table email (addr VARCHAR(20))";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql);
            stmt.close();
            System.out.println();
            System.out.println("EmailAddrTable --- Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAddr(String emailAddr) {
        String insert_sql = String.format("insert into email (addr) VALUES ('%s')", emailAddr);
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(insert_sql);
            stmt.close();
            System.out.println("EmailAddrInsert --- Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAddr() {
        getConnect();
        List<String> emails = new ArrayList<String>();
        String get_sql = "select * from email";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(get_sql);
            while (rs.next()) {
                emails.add(rs.getString("addr"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return emails;
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

    public void deleteTable() {
        String delete_sql = "drop table email";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(delete_sql);
            stmt.close();
            //System.out.println(tableName + " drop Success!");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
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

    public static void main(String[] args) {
        EmailToDB emailToDB = new EmailToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        emailToDB.getConnect();
        emailToDB.deleteTable();
        emailToDB.createAddrTable();
        emailToDB.closeAll();
    }
}
