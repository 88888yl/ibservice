package dispatch.database;

import org.apache.poi.ss.usermodel.*;
import utils.FindExcels;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl
 * on 2015/1/23.
 */
public class ImportDBfromDispatch {
    private String path = GlobalVariables.dispatchPath;

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public ImportDBfromDispatch(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createTablesFromSheets() {
        getConnect();
        deleteTable();
        List<String> columnNames = getColumnNames();
        StringBuilder create_sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();
        for (String columnName : columnNames) {
            subSql.append("\"").append(columnName).append("\"").append(" varchar2(2000),");
        }
        String substring = subSql.substring(0, subSql.length() - 1);
        create_sql.append("create table \"Dispatch-All\" (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql.toString());
            stmt.close();
            System.out.println("Dispatch-All create success!");
        } catch (SQLException e) {
            e.printStackTrace();
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
                String hasData_sql = "select * from \"Dispatch-All\"";
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
        closeAll();
    }

    public List<List<String>> dispatchSearch(String id) {
        getConnect();
        List<List<String>> resultLists = new ArrayList<List<String>>();

        String search_sql = "select * from \"Dispatch-All\" where \"Dispatch Number\" like '%" + id + "%'";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(search_sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            List<String> tmpRows = new ArrayList<String>();
            for (int i = 1; i < size + 1; i++) {
                tmpRows.add(rsmd.getColumnLabel(i));
            }
            resultLists.add(tmpRows);
            while (rs.next()) {
                List<String> tmpRows2 = new ArrayList<String>();
                for (int i = 1; i < size + 1; i++) {
                    String value = rs.getString(rsmd.getColumnLabel(i));
                    tmpRows2.add(value == null ? "" : value);
                }
                resultLists.add(tmpRows2);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return resultLists;
    }

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();

        FindExcels findExcels = new FindExcels();
        String[] excels = findExcels.getExcelsName(path);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + excels[0]);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
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
                if (columnNames.get(i).contains("Total")) {
                    columnNames.set(i, columnNames.get(i).split("and")[0] + columnNames.get(i).split("and ")[1]);
                    columnNames.set(i, columnNames.get(i).substring(1, columnNames.get(i).length() - 1));
                }
                if (columnNames.get(i).equals(" Sited System Local Identifier ")) {
                    columnNames.set(i, "Sited System Local Identifier");
                }
                if (columnNames.get(i).equals(" System Age in days (call-install) ")) {
                    columnNames.set(i, "SysAge in days(call-install)");
                }
                if (columnNames.get(i).equals(" System Age in months (call - install) ")) {
                    columnNames.set(i, "SysAge in months(call-install)");
                }
                if (columnNames.get(i).equals(" System Age in months (latest call - install) ")) {
                    columnNames.set(i, "SysAge in months(latest call)");
                }
                if (columnNames.get(i).equals(" Service Perf System Component ")) {
                    columnNames.set(i, "Service Perf System Component");
                }
            }
            columnNames.set(i, trimStr(columnNames.get(i)));
        }
        return columnNames;
    }

    private List<List<String>> addRows(String excel) {
        List<List<String>> rows = new ArrayList<List<String>>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + excel);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
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
        insert_sql.append("insert into \"Dispatch-All\" (")
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
            System.out.println("Dispatch-All: insert success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateInsert(List<List<String>> rows) {
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
        insert_sql.append("insert into \"Dispatch-All\" (")
                .append(substring).append(") select ").append(substring2)
                .append(" from dual where not exists(select \"Dispatch Number\" from \"Dispatch-All\" where (\"Dispatch Number\"=?))");
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
                pst.setString(columnNames.size() + 1, row.get(22));
                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println("Dispatch-All: insert success!");
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

    private void deleteTable() {
        String delete_sql = "drop table \"Dispatch-All\"";
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

//    public static void main(String[] args) {
//        ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
//                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
//        importDBfromDispatch.createTablesFromSheets();
//        importDBfromDispatch.insertTables();
//    }
}
