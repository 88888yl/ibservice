package scr.database;

import utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 2014/12/15.
 */
public class UpdateSCR {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> resultList = null;
    private String tableName = GlobalVariables.totalSCRTable;

    private String srcFilePath = GlobalVariables.scrPath + GlobalVariables.scrTableName;

    public UpdateSCR(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public int UpdateAllChanges() {
        getConnect();

        if (resultList.get(0).size() > 25) {
            for (int i = 25; i < resultList.get(0).size(); i++) {
                String add_sql = String.format("alter table %s add \"%s\" varchar2(500)", tableName, resultList.get(0).get(i));
                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(add_sql);
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
        }

        for (int i = 1; i < resultList.size(); i++) {
            for (int j = 25; j < resultList.get(0).size(); j++) {
                if (resultList.get(i).get(j) != null && !resultList.get(i).get(j).isEmpty()) {
                    String add_result_sql = String.format("update total_scr set \"%s\"='%s' where \"Number\"='%s'",
                            resultList.get(0).get(j), resultList.get(i).get(j), resultList.get(i).get(0));
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

        String count_sql = "select COLUMN_NAME from all_tab_columns where TABLE_NAME = 'TOTAL_SCR' order by column_id";
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
        System.out.println("------: total scr table have " + colNames.size() + " columns");
        for (int i = 25; i < colNames.size(); i++) {
            String reset_sql = "alter table " + tableName + " drop (\"" + colNames.get(i) + "\")";
            try {
                stmt = con.createStatement();
                stmt.executeQuery(reset_sql);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("------: reset total scr success!");
        closeAll();
    }

    public String updateSCRfromExcel() {
        ExcelLoader loader = new ExcelLoader(srcFilePath);
        List<List<String>> rows = loader.loadData(0);
        List<String> scrNumberList = new ArrayList<String>();
        List<String> scrTitle = rows.get(0);
        for (int i = 0; i < scrTitle.size(); i++) {
            String scr_tmp = scrTitle.get(i);
            if (scr_tmp.length() > 30) {
                scr_tmp = scr_tmp.substring(scr_tmp.lastIndexOf('.') + 1, scr_tmp.length());
                scrTitle.set(i, scr_tmp);
            }
            if (scr_tmp.length() > 30) {
                scr_tmp = scr_tmp.substring(0, scr_tmp.indexOf(' '));
                scrTitle.set(i, scr_tmp);
            }
        }

        for (int i = 1; i < rows.size(); i++) {
            scrNumberList.add(rows.get(i).get(0));
        }

        getConnect();

        String find_sql = "select \"Number\" from total_scr";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(find_sql);
            while (rs.next()) {
                for (int i = 0; i < scrNumberList.size(); i++) {
                    if (rs.getString("Number").equals(scrNumberList.get(i))) {
//                        StringBuilder update_sql = new StringBuilder();
//                        StringBuilder sub_update_sql = new StringBuilder();
//                        update_sql.append("update total_scr set ");
//                        for (int j = 0; j < scrTitle.size(); j++) {
//                            String aScrTitle = scrTitle.get(j);
//                            sub_update_sql.append("\"").append(aScrTitle).append("\"=\'").append(rows.get(i + 1).get(j)).append("\',");
//                        }
//                        update_sql.append(sub_update_sql.substring(0, sub_update_sql.length() - 1))
//                                .append(" where \"Number\"=\'").append(rs.getString("Number")).append("\'");
//
//                        Statement statement = con.createStatement();
//                        statement.executeUpdate(update_sql.toString());
//                        statement.close();
                        scrNumberList.set(i, "none");
                    }
                }
            }
            rs.close();
            stmt.close();
//            System.out.println("------: update exist from SCR.xls success!");
        } catch (SQLException ignored) {
        }

        int n = 0;
        try {
            StringBuilder sub_insert_sql1 = new StringBuilder();
            StringBuilder sub_insert_sql2 = new StringBuilder();
            for (String aCsoTitle : scrTitle) {
                sub_insert_sql1.append("\"").append(aCsoTitle).append("\",");
                sub_insert_sql2.append("?,");
            }
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement("insert into total_scr (" + sub_insert_sql1.substring(0, sub_insert_sql1.length() - 1) + ") values (" + sub_insert_sql2.substring(0, sub_insert_sql2.length() - 1) + ")");

            for (int i = 0; i < scrNumberList.size(); i++) {
                if (!scrNumberList.get(i).equals("none")) {
                    n++;
                    for (int j = 0; j < scrTitle.size(); j++) {
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
        System.out.println("-------: update new scr from SCR.xls success!");

        TotalSCRtoDB totalSCRtoDB = new TotalSCRtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        totalSCRtoDB.initYearFW();

        closeAll();
        return "update " + n + " rows scr from SCR.xls.";
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

    public static void main(String[] args) {
        UpdateSCR updateSCR = new UpdateSCR(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        updateSCR.updateSCRfromExcel();
    }
}
