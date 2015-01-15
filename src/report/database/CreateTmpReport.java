package report.database;

import cso.database.CSOReportToDB;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;

import java.sql.*;

/**
 * Created by myl on 2015/1/14.
 */
public class CreateTmpReport {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private String id;
    private String[] itemArray;

    public CreateTmpReport(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void initReport() {
        getConnect();

        if (id != null && itemArray != null) {
            if (id.equals("cso_report")) {
                deleteTmp("cso_tmp");
                deleteTmp("cso_report_tmp");

                String create_sql = "create table cso_tmp as select * from total_cso where 1=0";
                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(create_sql);
                    for (int i = 0; i < itemArray.length; i++) {
                        String insert_sql = "insert into cso_tmp select * from total_cso where \"CSO Number\"=\'" + itemArray[i] + "\'";
                        stmt.executeQuery(insert_sql);
                    }
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("cso tmp table init --- success!");

                CSOReportToDB csoReportToDB = new CSOReportToDB(
                        GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
                csoReportToDB.getConnect();
                csoReportToDB.deleteCSOReportTable("cso_report_tmp");
                csoReportToDB.createCSOReportTable("cso_report_tmp");
                csoReportToDB.insertCSOReportData("cso_tmp", "cso_report_tmp");
                csoReportToDB.closeAll();
                System.out.println("cso tmp report init --- success!");
            }
            if (id.equals("scr_report")) {
                deleteTmp("scr_tmp");
                deleteTmp("scr_report_tmp");

                String create_sql = "create table scr_tmp as select * from total_scr where 1=0";
                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(create_sql);
                    for (int i = 0; i < itemArray.length; i++) {
                        String insert_sql = "insert into scr_tmp select * from total_scr where \"Number\"=\'" + itemArray[i] + "\'";
                        stmt.executeQuery(insert_sql);
                    }
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("scr tmp table init --- success!");

                SCRReportToDB scrReportToDB = new SCRReportToDB(
                        GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
                scrReportToDB.getConnect();
                scrReportToDB.deleteSCRReportTable("scr_report_tmp");
                scrReportToDB.createSCRReportTable("scr_report_tmp");
                scrReportToDB.insertSCRReportData("scr_tmp", "scr_report_tmp");
                scrReportToDB.closeAll();
                System.out.println("scr tmp report init --- success!");
            }
            if (id.equals("cso_service")) {

            }
            if (id.equals("scr_service")) {

            }
        }

        closeAll();
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

    private void deleteTmp(String tmpTableName) {
        String delete_sql = "drop table " + tmpTableName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getItemArray() {
        return itemArray;
    }

    public void setItemArray(String[] itemArray) {
        this.itemArray = itemArray;
    }
}
