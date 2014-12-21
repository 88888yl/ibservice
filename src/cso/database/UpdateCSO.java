package cso.database;

import partfinder.utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-12-6.
 */
public class UpdateCSO {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> resultList = null;
    private String tableName = GlobalVariables.totalCSOTable;

    private String openCSOPath = GlobalVariables.csoPath + "Open_CSO.xlsx";
    private String closeCSOPath = GlobalVariables.csoPath + "Closed_CSO.xlsx";

    public UpdateCSO(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public int UpdateAllChanges() {
        getConnect();

        if (resultList.get(0).size() > 88) {
            for (int i = 88; i < resultList.get(0).size(); i++) {
                String add_sql = String.format("alter table %s add \"%s\" varchar2(500)", tableName, resultList.get(0).get(i));
                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(add_sql);
                    stmt.close();
                } catch (SQLException ignored) {}
            }
        }

        for (int i = 1; i < resultList.size(); i++) {
            for (int j = 88; j < resultList.get(0).size(); j++) {
                if (resultList.get(i).get(j) != null && !resultList.get(i).get(j).isEmpty()) {
                    String add_result_sql = String.format("update total_cso set \"%s\"='%s' where \"CSO Number\"='%s'",
                            resultList.get(0).get(j), resultList.get(i).get(j), resultList.get(i).get(8));
                    try {
                        stmt = con.createStatement();
                        stmt.executeQuery(add_result_sql);
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("-------: changes saved success!");
        closeAll();

        return 0;
    }

    public void resetCSO() {
        getConnect();

        String count_sql = "select COLUMN_NAME from all_tab_columns where TABLE_NAME = 'TOTAL_CSO' order by column_id";
        List<String> colNames = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(count_sql);
            while (rs.next()) {
                colNames.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("------: total cso table have " + colNames.size() + " columns");
        for (int i = 88; i < colNames.size(); i++) {
            String reset_sql = "alter table " + tableName + " drop (\"" + colNames.get(i) + "\")";
            try {
                stmt = con.createStatement();
                stmt.executeQuery(reset_sql);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("------: reset total cso success!");
        closeAll();
    }

    public void updateFromOpenCSO() {
        ExcelLoader loader = new ExcelLoader(openCSOPath);
        List<List<String>> rows = loader.loadData(0);
        List<String> csoNumberList = new ArrayList<String>();
        List<String> csoTitle = rows.get(0);
        for (int i = 1; i < rows.size(); i++) {
            csoNumberList.add(rows.get(i).get(8));
        }

        getConnect();

        String find_sql = "select \"CSO Number\" from total_cso";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(find_sql);
            while (rs.next()) {
                for (int i = 0; i < csoNumberList.size(); i++) {
                    if (rs.getString("CSO Number").equals(csoNumberList.get(i))) {
                        StringBuilder update_sql = new StringBuilder();
                        StringBuilder sub_update_sql = new StringBuilder();
                        update_sql.append("update total_cso set ");
                        for (int j = 0; j < csoTitle.size(); j++) {
                            sub_update_sql.append("\"").append(csoTitle.get(j)).append("\"=\'").append(rows.get(i + 1).get(j)).append("\',");
                        }
                        update_sql.append(sub_update_sql.substring(0, sub_update_sql.length() - 1))
                                .append(" where \"CSO Number\"=\'").append(rs.getString("CSO Number")).append("\'");
                        Statement statement = con.createStatement();
                        statement.executeUpdate(update_sql.toString());
                        statement.close();
                        csoNumberList.set(i, "none");
                    }
                }
            }
            rs.close();
            stmt.close();
            System.out.println("------: update exist from open_cso.xlsx cso success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            StringBuilder sub_insert_sql1 = new StringBuilder();
            StringBuilder sub_insert_sql2 = new StringBuilder();
            for (String aCsoTitle : csoTitle) {
                sub_insert_sql1.append("\"").append(aCsoTitle).append("\",");
                sub_insert_sql2.append("?,");
            }
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement("insert into total_cso (" + sub_insert_sql1.substring(0, sub_insert_sql1.length() - 1) + ") values (" + sub_insert_sql2.substring(0, sub_insert_sql2.length() - 1) + ")");
            for (int i = 0; i < csoNumberList.size(); i++) {
                if (!csoNumberList.get(i).equals("none")) {
                    for (int j = 0; j < csoTitle.size(); j++) {
                        if (rows.get(i + 1).get(j).isEmpty())
                            pst.setNull(j + 1, Types.VARCHAR);
                        else
                            pst.setString(j + 1, rows.get(i + 1).get(j));
                    }
                    pst.addBatch();
                }
            }
            pst.executeBatch();
            con.commit();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("-------: update new cso from open_cso.xlsx success!");

        TotalCSOtoDB totalCSOtoDB = new TotalCSOtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        totalCSOtoDB.initYearFW();

        closeAll();
    }

    public void updateFromCloseCSO() {
        ExcelLoader loader = new ExcelLoader(closeCSOPath);
        List<List<String>> rows = loader.loadData(0);
        List<String> csoNumberList = new ArrayList<String>();
        List<String> csoTitle = rows.get(0);
        for (int i = 0; i < csoTitle.size(); i++) {
            if (csoTitle.get(i).equals("Milestone"))
                csoTitle.set(i, "Milestone Status");
            if (csoTitle.get(i).equals("SR Status"))
                csoTitle.set(i, "Status");
            if (csoTitle.get(i).equals("Owner Last Name"))
                csoTitle.set(i, "Owner Name");
            if (csoTitle.get(i).equals("Root Cause"))
                csoTitle.set(i, "Root Cause Family");
        }
        for (int i = 1; i < rows.size(); i++) {
            csoNumberList.add(rows.get(i).get(8));
        }

        getConnect();

        String find_sql = "select \"CSO Number\" from total_cso";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(find_sql);
            while (rs.next()) {
                for (int i = 0; i < csoNumberList.size(); i++) {
                    if (rs.getString("CSO Number").equals(csoNumberList.get(i))) {
                        StringBuilder update_sql = new StringBuilder();
                        StringBuilder sub_update_sql = new StringBuilder();
                        update_sql.append("update total_cso set ");
                        for (int j = 0; j < csoTitle.size(); j++) {
                            sub_update_sql.append("\"").append(csoTitle.get(j)).append("\"=\'").append(rows.get(i + 1).get(j)).append("\',");
                        }
                        update_sql.append(sub_update_sql.substring(0, sub_update_sql.length() - 1))
                                .append(" where \"CSO Number\"=\'").append(rs.getString("CSO Number")).append("\'");
                        Statement statement = con.createStatement();
                        statement.executeUpdate(update_sql.toString());
                        statement.close();
                        csoNumberList.set(i, "none");
                    }
                }
            }
            rs.close();
            stmt.close();
            System.out.println("------: update exist cso from close_cso.xlsx success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            StringBuilder sub_insert_sql1 = new StringBuilder();
            StringBuilder sub_insert_sql2 = new StringBuilder();
            for (String aCsoTitle : csoTitle) {
                sub_insert_sql1.append("\"").append(aCsoTitle).append("\",");
                sub_insert_sql2.append("?,");
            }
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement("insert into total_cso (" + sub_insert_sql1.substring(0, sub_insert_sql1.length() - 1) + ") values (" + sub_insert_sql2.substring(0, sub_insert_sql2.length() - 1) + ")");
            for (int i = 0; i < csoNumberList.size(); i++) {
                if (!csoNumberList.get(i).equals("none")) {
                    for (int j = 0; j < csoTitle.size(); j++) {
                        if (rows.get(i + 1).get(j).isEmpty())
                            pst.setNull(j + 1, Types.VARCHAR);
                        else
                            pst.setString(j + 1, rows.get(i + 1).get(j));
                    }
                    pst.addBatch();
                }
            }
            pst.executeBatch();
            con.commit();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("-------: update new cso from close_cso.xlsx success!");

        TotalCSOtoDB totalCSOtoDB = new TotalCSOtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        totalCSOtoDB.initYearFW();

        closeAll();
    }

    private void getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getResultList() {
        return resultList;
    }

    public void setResultList(List<List<String>> resultList) {
        this.resultList = resultList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public static void main(String[] args) {
        UpdateCSO updateCSO = new UpdateCSO(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        //updateCSO.updateFromOpenCSO();
        updateCSO.resetCSO();
    }
}
