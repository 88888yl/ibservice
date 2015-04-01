package dispatch.database;

import org.apache.poi.ss.usermodel.*;
import utils.ExcelsUtils;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by myl
 * on 2015/1/23.
 */
public class ImportDBfromDispatch {
    private String path = GlobalVariables.dispatchPath;
    private String tableName = GlobalVariables.dispatchTableName;

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

    public String insertTables() {
        List<List<String>> rows;
        getConnect();
        ExcelsUtils excelsUtils = new ExcelsUtils();
        String[] excels = excelsUtils.getExcelsName(path);
        int n = 0;
        for (String excel : excels) {
            if (!excel.contains("(Up to date)")) {
                rows = addRows(excel);
                String hasData_sql = "select * from \"Dispatch-All\"";
                try {
                    stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(hasData_sql);
                    if (!rs.next()) {
                        n += firstInsert(rows);
                    } else {
                        n += updateInsert(rows);
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        closeAll();
        return "update " + n + " rows from Dispatch";
    }

    public List<String> dispatchCatalogue() {
        getConnect();
        List<String> result = new ArrayList<String>();
        String column_sql = "SELECT column_name FROM user_tab_columns WHERE table_name=\'Dispatch-All\'";
        String ColumnStr = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(column_sql);
            StringBuilder subColumns = new StringBuilder();
            subColumns.append("{");
            while (rs.next()) {
                subColumns.append("\'").append(rs.getString("COLUMN_NAME")).append("\':\"\",");
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

    public List<String> dispatchSearch(Map<String, String> stringMap) {
        if (stringMap.isEmpty()) {
            getConnect();
            List<String> result = new ArrayList<String>();
            StringBuilder subSqlBuider = new StringBuilder();
            String search_sql = "select * from \"Dispatch-All\"";
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(search_sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int size = rsmd.getColumnCount();
                List<String> tmpRows = new ArrayList<String>();
                StringBuilder subFields = new StringBuilder();
                StringBuilder subColumns = new StringBuilder();
                subFields.append("[");
                subColumns.append("[");
                for (int i = 1; i < size + 1; i++) {
                    subFields.append("{name: \'").append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
                    subColumns.append("{text: \'")
                            .append(rsmd.getColumnLabel(i).replaceAll("'", " "))
                            .append("\', sortable: true, dataIndex: \'")
                            .append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
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
        getConnect();
        List<String> result = new ArrayList<String>();
        StringBuilder subSqlBuider = new StringBuilder();
        for (Map.Entry<String, String> str : stringMap.entrySet()) {
            subSqlBuider.append("upper(\"").append(str.getKey()).append("\") like \'%").append(str.getValue().toUpperCase()).append("%\' and ");
        }
        String sub_sql = subSqlBuider.substring(0, subSqlBuider.length() - 4);
        String search_sql = "select * from \"Dispatch-All\" where " + sub_sql;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(search_sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            List<String> tmpRows = new ArrayList<String>();
            StringBuilder subFields = new StringBuilder();
            StringBuilder subColumns = new StringBuilder();
            subFields.append("[");
            subColumns.append("[");
            for (int i = 1; i < size + 1; i++) {
                subFields.append("{name: \'").append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
                subColumns.append("{text: \'")
                        .append(rsmd.getColumnLabel(i).replaceAll("'", " "))
                        .append("\', sortable: true, dataIndex: \'")
                        .append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
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

    public List<String> dispatchSysSearch(String sysId) {

        getConnect();
        List<String> result = new ArrayList<String>();
        StringBuilder subSqlBuider = new StringBuilder();
        String search_sql = "select * from \"Dispatch-All\" where \"Sited System Local Identifier\" like \'%"
                + sysId + "%\' and \"Cost Category Name\"=\'INSTALL\'";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(search_sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            List<String> tmpRows = new ArrayList<String>();
            StringBuilder subFields = new StringBuilder();
            StringBuilder subColumns = new StringBuilder();
            subFields.append("[");
            subColumns.append("[");
            for (int i = 1; i < size + 1; i++) {
                subFields.append("{name: \'").append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
                subColumns.append("{text: \'")
                        .append(rsmd.getColumnLabel(i).replaceAll("'", " "))
                        .append("\', sortable: true, dataIndex: \'")
                        .append(rsmd.getColumnLabel(i).replaceAll("'", " ")).append("\'},");
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

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();

//        ExcelsUtils excelsUtils = new ExcelsUtils();
//        String[] excels = excelsUtils.getExcelsName(path);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + tableName);
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

    private int firstInsert(List<List<String>> rows) {
        int n = 0;
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
                n++;
            }
            pst.executeBatch();
            con.commit();
            System.out.println("Dispatch-All: insert success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n;
    }

    private int updateInsert(List<List<String>> rows) {
        int n = 0;
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
                n++;
            }
            pst.executeBatch();
            con.commit();
            System.out.println("Dispatch-All: update success!");
            fileRename(path);
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n;
    }

    public Object[] getReport2(String[] dispatchNumbers) {
        getConnect();
        Object[] result = new Object[2];
        List<List<Integer>> reportList = new ArrayList<List<Integer>>();
        List<String> psiCodeList = new ArrayList<String>();

        Map<String, List<String>> productMap = new HashMap<String, List<String>>();

        for (String dispatchNumber : dispatchNumbers) {
            String search_sql = "select \"Psi Code\", \"Process Date\" " +
                    "from \"Dispatch-All\" where \"Dispatch Number\"=\'" +
                    dispatchNumber + "\' and \"Cost Category Name\"=\'INSTALL\'";
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(search_sql);
                if (rs.next()) {
                    String year = rs.getString("Process Date").substring(0, 4);
                    String psiCode = rs.getString("Psi Code");
                    if (!psiCodeList.contains(psiCode)) {
                        psiCodeList.add(psiCode);
                    }
                    if (productMap.get(year) == null) {
                        productMap.put(year, new ArrayList<String>());
                    }
                    if (productMap.get(year).isEmpty()) {
                        productMap.get(year).add(psiCode + "@" + 1);
                    } else {
                        int index;
                        if ((index = isContainPsiCode(productMap.get(year), psiCode)) != -1) {
                            int num = Integer.parseInt(productMap.get(year).get(index).split("@")[1]) + 1;
                            productMap.get(year).set(index, psiCode + "@" + num);
                        } else {
                            productMap.get(year).add(psiCode + "@" + 1);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : productMap.entrySet()) {
            List<Integer> row = new ArrayList<Integer>();
            row.add(Integer.parseInt(entry.getKey()));
            for (String tmp : entry.getValue()) {
                row.add(Integer.parseInt(tmp.split("@")[1]));
            }
            reportList.add(row);
        }
        closeAll();

        result[0] = psiCodeList;
        result[1] = reportList;
        return result;
    }

    private int isContainPsiCode(List<String> list, String psiCode) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(psiCode)) {
                return i;
            }
        }
        return -1;
    }

    public List<List<String>> getReport(String[] dispatchNumbers) {
        getConnect();
        List<List<String>> reportList = new ArrayList<List<String>>();

        Map<String, Integer> productMap = new HashMap<String, Integer>();

        for (String dispatchNumber : dispatchNumbers) {
            String search_sql = "select \"Psi Code\", \"Process Date\" " +
                    "from \"Dispatch-All\" where \"Dispatch Number\"=\'" +
                    dispatchNumber + "\' and \"Cost Category Name\"=\'INSTALL\'";
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(search_sql);
                if (rs.next()) {
                    String year = rs.getString("Process Date").substring(0, 4);
                    String psiCode = rs.getString("Psi Code");
                    String productKey = year + "@" + psiCode;
                    if (productMap.get(productKey) == null) {
                        productMap.put(productKey, 1);
                    } else {
                        int tmpSum = productMap.get(productKey) + 1;
                        productMap.put(productKey, tmpSum);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Map.Entry<String, Integer> entry : productMap.entrySet()) {
            List<String> row = new ArrayList<String>();
            String[] info = entry.getKey().split("@");
            row.add(info[0]);
            row.add(info[1]);
            row.add(entry.getValue().toString());
            reportList.add(row);
        }
        closeAll();
        return reportList;
    }

    public String getTree() {
        getConnect();
        Map<String, Map<String, List<String>>> treeMapList = new HashMap<String, Map<String, List<String>>>();
        String sql = "select \"Dispatch Number\", \"Psi Code\",\"Psi Description\" from \"Dispatch-All\"";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("Psi Code") != null) {
                    String code = rs.getString("Psi Code");
                    String desc = rs.getString("Psi Description");
                    String num = rs.getString("Dispatch Number");

                    if (code != null) code = code.replaceAll("\\n", "").replaceAll("\"", "");
                    if (desc != null) desc = desc.replaceAll("\\n", "").replaceAll("\"", "");
                    if (num != null) num = num.replaceAll("\\n", "").replaceAll("\"", "");

                    if (treeMapList.get(code) == null) {
                        treeMapList.put(code, new HashMap<String, List<String>>());
                    }
                    if (treeMapList.get(code).get(desc) == null) {
                        treeMapList.get(code).put(desc, new ArrayList<String>());
                    }
                    treeMapList.get(code)
                            .get(desc)
                            .add(num);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<String, List<String>>> subMap : treeMapList.entrySet()) {
            if (subMap != null) {
                sb.append("{\"text\": \"").append(subMap.getKey())
                        .append("\",\"checked\": false,\"expanded\": false,\"children\": [");
                StringBuilder subSb = new StringBuilder();
                for (Map.Entry<String, List<String>> list : subMap.getValue().entrySet()) {
                    subSb.append("{\"text\": \"").append(list.getKey())
                            .append("\",\"checked\": false,\"expanded\": false,\"children\": [");
                    StringBuilder subSubSb = new StringBuilder();
                    for (String numberStr : list.getValue()) {
                        subSubSb.append("{\"text\": \"")
                                .append(numberStr)
                                .append("\",\"checked\": false,\"leaf\": true},");
                    }
                    subSb.append(subSubSb.substring(0, subSubSb.length() - 1)).append("]},");
                }
                sb.append(subSb.substring(0, subSb.length() - 1)).append("]},");
            }
        }
        closeAll();
        return "[" + sb.substring(0, sb.length() - 1) + "]";
    }

    private void fileRename(String path) {
        String[] excels = new ExcelsUtils().getExcelsName(path);
        for (String excel : excels) {
            if (!excel.contains("(Up to date)")) {
                File excelFile = new File(path + excel);
                if (!excelFile.renameTo(new File(path + "(Up to date)" + excel))) {
                    System.out.println("Rename the excel \"" + excel + "\" failed.");
                }
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

    public static void main(String[] args) {
        ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
//        importDBfromDispatch.createTablesFromSheets();
        importDBfromDispatch.insertTables();
    }
}
