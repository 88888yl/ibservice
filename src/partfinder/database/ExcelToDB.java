package partfinder.database;

import org.apache.poi.ss.usermodel.*;
import utils.ExcelLoader;
import utils.FindExcels;
import utils.GlobalVariables;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 9/29/14.
 * Study code
 */
public class ExcelToDB {
    private String URL;
    private String USER;
    private String PWD;

    private String excelPath;
    private String tableName;
    private String tableDesc;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public ExcelToDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public ExcelToDB(String URL, String USER, String PWD, String excelPath, String tableName, String tableDesc) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
        this.excelPath = excelPath;
        this.tableName = tableName;
        this.tableDesc = tableDesc;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getExcelPath() {
        return excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public Connection getConnect() {
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

    public void createTablesFromExcel2() {
        List<String> columnNames = getColumnNames();
        StringBuilder create_sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();
        for (String columnName : columnNames) {
            if (columnName.equals("Row Number") || columnName.equals("Level")) {
                subSql.append("\"").append(columnName).append("\"").append(" Number(10),");
            } else {
                subSql.append("\"").append(columnName).append("\"").append(" varchar2(2000),");
            }
        }
        String substring = subSql.substring(0, subSql.length() - 1);
        create_sql.append("create table \"").append(tableName).append("\" (\"Father Number\" Number(10),").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql.toString());
            stmt.close();
            System.out.println();
            System.out.println(tableName + "     Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTableFromExcel() {
        String sql;
        sql = "CREATE TABLE " + tableName + " " +
                "(\"Row_Number\" Number(10) PRIMARY KEY, " +
                "\"Father_Number\" Number(10)," +
                "\"Level_Number\" Number(10)," +
                "\"Mark_Number\" VARCHAR(100)," +
                "\"Reference_Designator\" VARCHAR(2000)," +
                "\"Type\" VARCHAR(100)," +
                "\"Name\" VARCHAR(100)," +
                "\"Revision\" VARCHAR(100)," +
                "\"State\" VARCHAR(100)," +
                "\"UOM\" VARCHAR(100)," +
                "\"Quantity\" VARCHAR(2000)," +
                "\"Description\" VARCHAR(2000)," +
                "\"Manufacturer_Equivalent_part\" VARCHAR(2000)," +
                "\"Manufacturer\" VARCHAR(2000)," +
                "\"RDO\" VARCHAR(100)," +
                "\"Essential_To\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println();
            System.out.println(tableName + "     Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTableFromExcel() {
        String sql;
        sql = "drop TABLE " + tableName;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            //System.out.println(tableName + " drop Success!");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void excelToTable() {
        String sql;
        StringBuffer sqls = new StringBuffer();

        ExcelLoader loader = new ExcelLoader(excelPath);
        List<List<String>> rows = loader.loadData(0);

        List<String> rootRow = rows.get(7);

        sql = "insert into " + tableName + " (\"Row_Number\",\"Father_Number\",\"Level_Number\",\"Type\",\"Name\",\"Revision\",\"Description\") values (0,0,0" +
                ",\'" + rootRow.get(1) + "\'" +
                ",\'" + rootRow.get(2) + "\'" +
                ",\'" + rootRow.get(3) + "\'" +
                ",\'" + tableDesc + "\')";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (rows.get(8).size() == 15) {
            sqls.append("insert into ").append(tableName).append(" (\"Row_Number\",\"Father_Number\",\"Level_Number\"," +
                    "\"Mark_Number\",\"Reference_Designator\",\"Type\",\"Name\",\"Revision\"," +
                    "\"State\",\"UOM\",\"Quantity\",\"Description\",\"Manufacturer_Equivalent_part\"," +
                    "\"Manufacturer\",\"RDO\",\"Essential_To\")" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        } else {
            sqls.append("insert into ").append(tableName).append(" (\"Row_Number\",\"Father_Number\",\"Level_Number\"," +
                    "\"Mark_Number\",\"Reference_Designator\",\"Type\",\"Name\",\"Revision\"," +
                    "\"State\",\"UOM\",\"Quantity\",\"Description\",\"Manufacturer_Equivalent_part\"," +
                    "\"Manufacturer\",\"RDO\")" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        }
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sqls.toString());
            int i = 0;
            for (List<String> row : rows.subList(8, rows.size())) {

                setIntNull(1, row.get(0), pst);
                setIntNull(2, "0", pst);
                setIntNull(3, row.get(1), pst);
                setStringNull(4, row.get(2), pst);
                setStringNull(5, row.get(3), pst);
                setStringNull(6, row.get(4), pst);
                setStringNull(7, row.get(5), pst);
                setStringNull(8, row.get(6), pst);
                setStringNull(9, row.get(7), pst);
                setStringNull(10, row.get(8), pst);
                setDoutleNull(11, row.get(9), pst);
                setStringNull(12, row.get(10), pst);
                setStringNull(13, row.get(11), pst);
                setStringNull(14, row.get(12), pst);
                setStringNull(15, row.get(13), pst);
                if (row.size() == 15) {
                    setStringNull(16, row.get(14), pst);
                }

                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println("----------------: all rows insert success!");
            pst.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excelToTable2() {
        List<List<String>> rows;
        rows = addRows();
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
        insert_sql.append("insert into \"").append(tableName).append("\" (")
                .append(substring).append(") values (").append(substring2).append(")");
        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(insert_sql.toString());
            for (List<String> row : rows) {
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
            System.out.println("----------------: all rows insert success!");
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<List<String>> addRows() {
        List<List<String>> rows = new ArrayList<List<String>>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(excelPath);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 9; i < sheet.getLastRowNum(); i++) {
                List<String> rowList = new ArrayList<String>();
                for (int j = 0; j < sheet.getRow(7).getLastCellNum(); j++) {
                    Cell cell = sheet.getRow(i).getCell(j) == null ? null : sheet.getRow(i).getCell(j);
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

    public void closeAll() {
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

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(excelPath);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 0; j < sheet.getRow(7).getLastCellNum(); j++) {
                Cell cell = sheet.getRow(7).getCell(j);
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
        return columnNames;
    }

    private void setStringNull(int i, String str, PreparedStatement pst) throws SQLException {
        if (str.isEmpty()) {
            pst.setNull(i, Types.VARCHAR);
        } else {
            pst.setString(i, str);
        }
    }

    private void setIntNull(int i, String str, PreparedStatement pst) throws SQLException {
        if (str.isEmpty()) {
            pst.setNull(i, Types.INTEGER);
        } else {
            pst.setInt(i, Integer.valueOf(str));
        }
    }

    private void setDoutleNull(int i, String str, PreparedStatement pst) throws SQLException {
        if (str.isEmpty()) {
            pst.setNull(i, Types.DOUBLE);
        } else {
            pst.setDouble(i, Double.valueOf(str));
        }
    }
}
