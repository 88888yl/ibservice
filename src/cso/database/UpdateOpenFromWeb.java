package cso.database;

import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-11-16.
 */
public class UpdateOpenFromWeb {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> resultList = null;
    private String tableName = GlobalVariables.totalCSOTable;

    public UpdateOpenFromWeb(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public int UpdateAllChanges() {
        getConnect();
        String delete_sql = String.format("delete %s", tableName);
        try {
            stmt = con.createStatement();
            stmt.executeQuery(delete_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (resultList.get(0).size() > 88) {
            for (int i = 88; i < resultList.get(0).size(); i++) {
                String add_sql = String.format("alter table %s add \"%s\" varchar2(500)", tableName, resultList.get(0).get(i));
                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(add_sql);
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        String sql = String.format("select column_name from all_tab_columns where TABLE_NAME = '%s' order by column_id", tableName);
        List<String> columnNames = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                columnNames.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder sqls = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();

        for (String row : columnNames) {
            subSql1.append("\"").append(row).append("\",");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        for (int i = 0; i < columnNames.size(); i++) {
            subSql2.append("?,");
        }
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        sqls.append("insert into total_cso (").append(substring).append(") values (").append(substring2).append(")");

        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sqls.toString());
            for (List<String> row : resultList.subList(1, resultList.size())) {
                for (int j = 0; j < columnNames.size(); j++) {
                    if (row.get(j) == null || row.get(j).isEmpty())
                        pst.setNull(j + 1, Types.VARCHAR);
                    else
                        pst.setString(j + 1, row.get(j));
                }
                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println("----------------: " + resultList.size() + " rows update success!");
            pst.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return 0;
    }

    private Connection getConnect() {
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
}
