package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/1.
 */
public class ExportToExcel {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private String store;

    public ExportToExcel(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }


    public List<List<String>> getRowsFromStr(String tableName) {
        List<List<String>> rows = new ArrayList<List<String>>();
        String subStore = store.substring(1, store.length() - 1);
        String[] subStores = subStore.split("]");

        getConnect();
        String column_sql = "select * from \"" + tableName + "\"";
        List<String> rowTitle = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(column_sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            for (int i = 1; i < size + 1; i++) {
                rowTitle.add(rsmd.getColumnLabel(i));
            }
            rows.add(rowTitle);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();

        String str0 = subStores[0].substring(1, subStores[0].length());
        String[] subCellsStr0 = str0.split("\",\"");
        List<String> row0 = new ArrayList<String>();
        row0.add(subCellsStr0[0].substring(1, subCellsStr0[0].length()));
        row0.addAll(Arrays.asList(subCellsStr0).subList(1, subCellsStr0.length));
        rows.add(row0);

        for (int i = 1; i < subStores.length; i++) {
            String strRow = subStores[i].substring(2, subStores[i].length());
            String[] subCellsStr = strRow.split("\",\"");
            List<String> row = new ArrayList<String>();
            row.add(subCellsStr[0].substring(1, subCellsStr[0].length()));
            row.addAll(Arrays.asList(subCellsStr0).subList(1, subCellsStr0.length));
            rows.add(row);
        }

        return rows;
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
