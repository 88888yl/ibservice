package partfinder.database;

import partfinder.utils.GetTableInfo;
import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by myl on 2015/2/6.
 */
public class PartFinderCatalogueFromDB {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public PartFinderCatalogueFromDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public List<String> partFinderCatalogue() {
        getConnect();
        List<String> result = new ArrayList<String>();
        String column_sql = "SELECT column_name FROM user_tab_columns WHERE table_name=\'BOM_5188881_8\'";
        String ColumnStr = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(column_sql);
            StringBuilder subColumns = new StringBuilder();
            subColumns.append("{");
            while (rs.next()) {
                String value = rs.getString("COLUMN_NAME");
                subColumns.append("\'").append(value == null ? "" : value.replaceAll("\'", " ")).append("\':\"\",");
            }
            ColumnStr = subColumns.substring(0, subColumns.length() - 1) + "}";
            result.add(ColumnStr);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return result;
    }

    public List<String> partFinderSearch(Map<String, String> stringMap, String tableName) {
        getConnect();
        List<String> result = new ArrayList<String>();
        StringBuilder subSqlBuider = new StringBuilder();
        String sub_sql = null;
        if (!stringMap.isEmpty()) {
            for (Map.Entry<String, String> str : stringMap.entrySet()) {
                subSqlBuider.append("\"").append(str.getKey()).append("\" like \'%").append(str.getValue()).append("%\' and ");
            }
            sub_sql = subSqlBuider.substring(0, subSqlBuider.length() - 4);
        }
        String search_sql = null;
        if (!tableName.equals("all")) {
            if (stringMap.isEmpty()) {
                return null;
            } else {
                search_sql = "select * from \"" + tableName + "\" where " + sub_sql + " ORDER by \"Row_Number\"";
            }
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(search_sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int size = rsmd.getColumnCount();
                StringBuilder subFields = new StringBuilder();
                StringBuilder subColumns = new StringBuilder();
                subFields.append("[");
                subColumns.append("[");
                for (int i = 1; i < size + 1; i++) {
                    subFields.append("{name: \'").append(rsmd.getColumnLabel(i).replaceAll("\'", " ")).append("\'},");
                    subColumns.append("{text: \'")
                            .append(rsmd.getColumnLabel(i).replaceAll("\'", " "))
                            .append("\', sortable: true, dataIndex: \'")
                            .append(rsmd.getColumnLabel(i).replaceAll("\'", " ")).append("\'},");
                }
                String fields = (subFields.substring(0, subFields.length() - 1) + "]")
                        .replaceAll("\"", " ").replaceAll("\\n", "");
                String columns = (subColumns.substring(0, subColumns.length() - 1) + "]")
                        .replaceAll("\"", " ").replaceAll("\\n", "");
                result.add(fields);
                result.add(columns);

                StringBuilder subDummyData = new StringBuilder();
                subDummyData.append("[");
                while (rs.next()) {
                    StringBuilder subDummyData2 = new StringBuilder();
                    subDummyData2.append("[");
                    for (int i = 1; i < size + 1; i++) {
                        String value = rs.getString(rsmd.getColumnLabel(i));
                        subDummyData2.append("\'").append(value == null ? "" : value.replaceAll("\'", " ")).append("\',");
                    }
                    subDummyData.append(subDummyData2.substring(0, subDummyData2.length() - 1)).append("],");
                }
                String dummyData = (subDummyData.substring(0, subDummyData.length() - 1) + "]")
                        .replaceAll("\"", " ").replaceAll("\\n", "");
                result.add(dummyData);
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            GetTableInfo getTableInfo = new GetTableInfo();
            getTableInfo.getConnect();
            List<String> tableNames = getTableInfo.getTableNames();
            getTableInfo.closeAll();
            StringBuilder dummyData = new StringBuilder();
            for (int n = 0; n < tableNames.size(); n++) {
                if (stringMap.isEmpty()) {
                    return null;
                } else {
                    search_sql = "select * from \"" + tableNames.get(n) + "\" where " + sub_sql + " ORDER by \"Row_Number\"";
                }
                try {
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(search_sql);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int size = rsmd.getColumnCount();

                    if (n == 0) {
                        StringBuilder subFields = new StringBuilder();
                        StringBuilder subColumns = new StringBuilder();
                        subFields.append("[");
                        subColumns.append("[");
                        for (int i = 1; i < size + 1; i++) {
                            subFields.append("{name: \'").append(rsmd.getColumnLabel(i).replaceAll("\'", " ")).append("\'},");
                            subColumns.append("{text: \'")
                                    .append(rsmd.getColumnLabel(i).replaceAll("\'", " "))
                                    .append("\', sortable: true, dataIndex: \'")
                                    .append(rsmd.getColumnLabel(i).replaceAll("\'", " ")).append("\'},");
                        }
                        String fields = (subFields.substring(0, subFields.length() - 1) + "]")
                                .replaceAll("\"", " ").replaceAll("\\n", "");
                        String columns = (subColumns.substring(0, subColumns.length() - 1) + "]")
                                .replaceAll("\"", " ").replaceAll("\\n", "");
                        result.add(fields);
                        result.add(columns);
                    }

                    StringBuilder subDummyData = new StringBuilder();
                    while (rs.next()) {
                        StringBuilder subDummyData2 = new StringBuilder();
                        subDummyData2.append("[");
                        for (int i = 1; i < size + 1; i++) {
                            String value = rs.getString(rsmd.getColumnLabel(i));
                            subDummyData2.append("\'").append(value == null ? "" : value.replaceAll("\'", " ")).append("\',");
                        }
                        subDummyData.append(subDummyData2.substring(0, subDummyData2.length() - 1)).append("],");
                    }
                    dummyData.append(subDummyData.toString().replaceAll("\"", " ").replaceAll("\\n", ""));
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            result.add("[" + dummyData.toString().substring(0, dummyData.length() - 1) + "]");
        }
        closeAll();
        return result;
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
