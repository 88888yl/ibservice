package complaints.database;

import org.apache.poi.ss.usermodel.*;
import utils.FindExcels;
import utils.GetExcelTableInfo;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void createTablesFromSheets2() {
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

    public void createTablesFromSheets() {
        getConnect();
        deleteAllSheetsTable();
        List<String> columnNames = getColumnNames();
        StringBuilder create_sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();
        for (String columnName : columnNames) {
            subSql.append("\"").append(columnName).append("\"").append(" varchar2(2000),");
        }
        String substring = subSql.substring(0, subSql.length() - 1);
        create_sql.append("create table \"Complaints-All\" (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql.toString());
            stmt.close();
            System.out.println("Complaints-All create success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
    }

    public void insertTables2() {
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

    public void insertTables() {
        List<List<String>> rows;
        getConnect();
        FindExcels findExcels = new FindExcels();
        String[] excels = findExcels.getExcelsName(path);
        for (String excel : excels) {
            if (!excel.contains("(Up to date)")) {
                rows = addRows(excel);
                String hasData_sql = "select * from \"Complaints-All\"";
                try {
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(hasData_sql);
                    if (!rs.next()) {
                        firstInsert(rows);
                    } else {
                        updateInsert(rows);
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<List<String>> addRows(String excel) {
        List<List<String>> rows = new ArrayList<List<String>>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + excel);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(1);
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                List<String> rowList = new ArrayList<String>();
                for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    String value = "";
                    if (cell == null) {
                        rowList.add("");
                    } else {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    value = DateFormat.getDateInstance().format(cell.getDateCellValue());
                                } else {
                                    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                                    nf.setGroupingUsed(false);
                                    value = String.valueOf(nf.format(cell.getNumericCellValue()));
                                }
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                switch (cell.getCachedFormulaResultType()) {
                                    case Cell.CELL_TYPE_STRING:
                                        value = cell.getStringCellValue();
                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        value = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    default:
                                }
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                value = String.valueOf(cell.getErrorCellValue());
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                break;
                            default:
                                break;
                        }
                        rowList.add(value);
                    }
                }
                rows.add(rowList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();

        FindExcels findExcels = new FindExcels();
        String[] excels = findExcels.getExcelsName(path);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + excels[0]);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(1);
            for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                Cell cell = sheet.getRow(0).getCell(j);
                if (cell == null) {
                    columnNames.add("empty-" + j);
                } else {
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        columnNames.add(String.valueOf(cell.getNumericCellValue()));
                    } else {
                        columnNames.add(String.valueOf(cell.getStringCellValue()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).length() > 30) {
                if (columnNames.get(i).equals("Additional Information Requested")) {
                    columnNames.set(i, "Additional Info Requested");
                }
            }
            columnNames.set(i, trimStr(columnNames.get(i)));
        }
        return columnNames;
    }

    public List<String> complaintsSearch(Map<String, String> stringMap) {
        if (stringMap.isEmpty()) {
            getConnect();
            List<String> result = new ArrayList<String>();
            StringBuilder subSqlBuider = new StringBuilder();
            StringBuilder ageSqlBuider = new StringBuilder();
            String search_sql;
            search_sql = "select * from \"Complaints-All\"";

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
                if (dummyData.equals("]")) return null;
                result.add(dummyData);
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeAll();
            return  result;
        }
        getConnect();
        List<String> result = new ArrayList<String>();
        StringBuilder subSqlBuider = new StringBuilder();
        for (Map.Entry<String, String> str : stringMap.entrySet()) {
            subSqlBuider.append("upper(\"").append(str.getKey()).append("\") like \'%").append(str.getValue().toUpperCase()).append("%\' and ");
        }
        String sub_sql = subSqlBuider.substring(0, subSqlBuider.length() - 4);
        String search_sql = "select * from \"Complaints-All\" where " + sub_sql;
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
            if (dummyData.equals("]")) return null;
            result.add(dummyData);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return result;
    }

    public List<String> complaintsCatalogue() {
        getConnect();
        List<String> result = new ArrayList<String>();
        String column_sql = "SELECT column_name FROM user_tab_columns WHERE table_name=\'Complaints-All\'";
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

    private void firstInsert(List<List<String>> rows) {
        StringBuilder insert_sql = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();
        List<String> columnNames = getColumnNames();
        for (String columnName : columnNames) {
            subSql1.append("\"").append(columnName).append("\",");
            subSql2.append("?,");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        insert_sql.append("insert into \"Complaints-All\" (")
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
            System.out.println("Complaints-All: insert success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateInsert(List<List<String>> rows) {

        List<String> columnNames = getColumnNames();

        System.out.println("start");
        for (List<String> row : rows.subList(1, rows.size())) {
            StringBuilder insert_sql = new StringBuilder();
            StringBuilder subSql1 = new StringBuilder();
            StringBuilder subSql2 = new StringBuilder();

            for (int i = 0; i < columnNames.size(); i++) {
                subSql1.append("\"").append(columnNames.get(i)).append("\",");
                subSql2.append("\'").append(trimStr(row.get(i).replaceAll("'", ""))).append("\',");
            }
            String substring = subSql1.substring(0, subSql1.length() - 1);
            String substring2 = subSql2.substring(0, subSql2.length() - 1);
            insert_sql.append("insert into \"Complaints-All\" (")
                    .append(substring).append(") select ").append(substring2)
                    .append(" from dual where not exists(select \"PR ID\" from \"Complaints-All\" where (\"PR ID\"=\'")
                    .append(row.get(0)).append("\'))");
//            System.out.println(insert_sql);
            try {
                stmt = con.createStatement();
                stmt.executeUpdate(insert_sql.toString());
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("end");
        System.out.println("Complaints-All: update success!");
        closeAll();

//        for (String columnName : columnNames) {
//            subSql1.append("\"").append(columnName).append("\",");
//            subSql2.append("?,");
//        }
//        String substring = subSql1.substring(0, subSql1.length() - 1);
//        String substring2 = subSql2.substring(0, subSql2.length() - 1);
//        insert_sql.append("insert into \"Complaints-All\" (")
//                .append(substring).append(") select ").append(substring2)
//                .append(" from dual where not exists(select \"PR ID\" from \"Complaints-All\" where (\"PR ID\"=?))");
//        try {
//            con.setAutoCommit(false);
//            PreparedStatement pst = con.prepareStatement(insert_sql.toString());
//            for (List<String> row : rows.subList(1, rows.size())) {
//                for (int j = 0; j < columnNames.size(); j++) {
//                    if (row.get(j).isEmpty())
//                        pst.setNull(j + 1, Types.VARCHAR);
//                    else
//                        pst.setString(j + 1, row.get(j));
//                }
//                pst.setString(columnNames.size() + 1, row.get(0));
//                pst.addBatch();
//            }
//            pst.executeBatch();
//            con.commit();
//            System.out.println("Complaints-All: update success!");
//            fileRename(path);
//            pst.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
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
        String delete_sql = "drop table \"Complaints-All\"";
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

    public static void main(String[] args) {
        ImportDBfromAllSheets importDBfromAllSheets = new ImportDBfromAllSheets(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        importDBfromAllSheets.createTablesFromSheets();
        importDBfromAllSheets.insertTables();
    }
}
