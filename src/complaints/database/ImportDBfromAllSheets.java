package complaints.database;

import utils.FindExcels;
import utils.GetExcelTableInfo;
import utils.GlobalVariables;

import java.io.File;
import java.sql.*;
import java.util.List;

/**
 * Created by myl
 * on 2015/1/22.
 */
public class ImportDBfromAllSheets {
    private String path = GlobalVariables.complaintsPath;

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    GetExcelTableInfo getExcelTableInfo = new GetExcelTableInfo(path);

    public ImportDBfromAllSheets(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createTablesFromSheets() {
        List<String> tableNames;
        List<String> columnNames;

        getConnect();
        deleteAllSheetsTable();

        tableNames = getExcelTableInfo.getAllTableNames();
        for (String tableName : tableNames) {
            StringBuilder create_sql = new StringBuilder();
            StringBuilder subSql = new StringBuilder();
            if (!tableName.contains("Instructions")) {
                columnNames = getExcelTableInfo.getColumnNames(tableName);
                for (String columnName : columnNames) {
                    if (columnName.length() > 30) {
                        if (columnName.equals("Additional Information Requested")) {
                            columnName = "Additional Info Requested";
                        }
                    }
                    subSql.append("\"").append(columnName).append("\"").append(" varchar2(2000),");
                }
                String substring = subSql.substring(0, subSql.length() - 1);
                create_sql.append("create table \"").append(tableName).append("\" (").append(substring).append(")");
                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate(create_sql.toString());
                    stmt.close();
                    System.out.println(tableName + " create success!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        closeAll();
    }

    public void insertTables() {
        List<String> tableNames;
        List<String> columnNames;
        List<List<String>> rows;

        getConnect();

        tableNames = getExcelTableInfo.getAllTableNames();
        for (String tableName : tableNames) {
            StringBuilder insert_sql = new StringBuilder();
            StringBuilder subSql1 = new StringBuilder();
            StringBuilder subSql2 = new StringBuilder();
            if (!tableName.contains("Instructions")) {
                columnNames = getExcelTableInfo.getColumnNames(tableName);
                rows = getExcelTableInfo.getRows(tableName);
                for (String columnName : columnNames) {
                    if (columnName.length() > 30) {
                        if (columnName.equals("Additional Information Requested")) {
                            columnName = "Additional Info Requested";
                        }
                    }
                    subSql1.append("\"").append(columnName).append("\",");
                }
                String substring = subSql1.substring(0, subSql1.length() - 1);
                for (int i = 0; i < columnNames.size(); i++) {
                    subSql2.append("?,");
                }
                String substring2 = subSql2.substring(0, subSql2.length() - 1);
                insert_sql.append("insert into \"").append(tableName).append("\" (")
                        .append(substring).append(") values (").append(substring2).append(")");
                try {
                    con.setAutoCommit(false);
                    PreparedStatement pst = con.prepareStatement(insert_sql.toString());
                    for (List<String> row : rows.subList(1, rows.size())) {
                        for (int j = 0; j < columnNames.size(); j++) {
                            if (row.get(j).isEmpty())
                                pst.setNull(j + 1, Types.VARCHAR);
                            else
                                pst.setString(j + 1, row.get(j));
                        }
                        pst.addBatch();
                    }
                    pst.executeBatch();
                    con.commit();
                    System.out.println(tableName + ": insert success!");
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        closeAll();
    }

    private void firstInsert(List<List<String>> rows, String tableName) {
        StringBuilder insert_sql = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();
        List<String> columnNames = getExcelTableInfo.getColumnNames(tableName);
        for (String columnName : columnNames) {
            subSql1.append("\"").append(columnName).append("\",");
            subSql2.append("?,");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        insert_sql.append("insert into ").append(tableName).append(" (")
                .append(substring).append(") values (").append(substring2).append(")");
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(insert_sql.toString());
            for (List<String> row : rows.subList(1, rows.size())) {
                for (int j = 0; j < columnNames.size(); j++) {
                    if (row.get(j).isEmpty())
                        pst.setNull(j + 1, Types.VARCHAR);
                    else
                        pst.setString(j + 1, row.get(j));
                }
                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println(tableName + ": insert success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateInsert(List<List<String>> rows, String tableName) {
        StringBuilder insert_sql = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();
        List<String> columnNames = getExcelTableInfo.getColumnNames(tableName);
        for (String columnName : columnNames) {
            subSql1.append("\"").append(columnName).append("\",");
            subSql2.append("?,");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        insert_sql.append("insert into ").append(tableName).append(" (")
                .append(substring).append(") select ").append(substring2).append(" from dual where not exists(select \"PR ID\" from ").append(tableName).append(" where (\"PR ID\"=?))");
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(insert_sql.toString());
            for (List<String> row : rows.subList(1, rows.size())) {
                for (int j = 0; j < columnNames.size(); j++) {
                    if (row.get(j).isEmpty())
                        pst.setNull(j + 1, Types.VARCHAR);
                    else
                        pst.setString(j + 1, row.get(j));
                }
                pst.setString(columnNames.size() + 1, row.get(0));
                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println(tableName + ": insert success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fileRename(String path) {
        String[] excels = new FindExcels().getExcelsName(path);
        for (String excel : excels) {
            if (!excel.contains("(Up to date)")) {
                File excelFile = new File(path + excel);
                excelFile.renameTo(new File(path + "(Up to date)" + excel));
            }
        }
    }

    private String trimStr(String s) {
        int i = s.length();
        int j = 0;
        int k = 0;
        char[] arrayOfChar = s.toCharArray();
        while ((j < i) && (arrayOfChar[(k + j)] <= ' '))
            ++j;
        while ((j < i) && (arrayOfChar[(k + i - 1)] <= ' '))
            --i;
        return (((j > 0) || (i < s.length())) ? s.substring(j, i) : s);
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

    private void deleteAllSheetsTable() {
        List<String> tableNames = getExcelTableInfo.getAllTableNames();
        for (String tableName : tableNames) {
            String delete_sql = "drop table \"" + tableName + "\"";
            try {
                stmt = con.createStatement();
                stmt.executeUpdate(delete_sql);
                stmt.close();
            } catch (SQLException ignored) {
            }
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

    public static void main(String[] args) {
        ImportDBfromAllSheets importDBfromAllSheets = new ImportDBfromAllSheets(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        importDBfromAllSheets.createTablesFromSheets();
        importDBfromAllSheets.insertTables();
    }
}
