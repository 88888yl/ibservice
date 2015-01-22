package partfinder.database;

import utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
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

    public void createTableFromExcel() {
        String sql;
        sql = "CREATE TABLE " + tableName + " " +
                "(\"Row_Number\" Number(8) PRIMARY KEY, " +
                "\"Father_Number\" NUMBER(8)," +
                "\"Level_Number\" Number(8) NOT NULL," +
                "\"Mark_Number\" VARCHAR(8)," +
                "\"Reference_Designator\" VARCHAR(1024)," +
                "\"Type\" VARCHAR(50)," +
                "\"Name\" VARCHAR(20)," +
                "\"Revision\" VARCHAR(8)," +
                "\"State\" VARCHAR(20)," +
                "\"UOM\" VARCHAR(20)," +
                "\"Quantity\" Number(8,1)," +
                "\"Description\" VARCHAR(1024)," +
                "\"Manufacturer_Equivalent_part\" VARCHAR(1024)," +
                "\"Manufacturer\" VARCHAR(1024)," +
                "\"RDO\" VARCHAR(8))";
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

        sql = "insert into " + tableName + " (\"Row_Number\",\"Father_Number\",\"Level_Number\",\"Type\",\"Name\"," +
                "\"Revision\",\"Description\",\"Manufacturer_Equivalent_part\",\"Manufacturer\",\"RDO\")" +
                " values (0," +
                "0," +
                rootRow.get(0) +
                ",\'" + rootRow.get(1) + "\'" +
                ",\'" + rootRow.get(2) + "\'" +
                "," + rootRow.get(3) +
                ",\'" + tableDesc + "\'" +
                ",\' \'" +
                ",\' \'" +
                ",\' \'" +
                ")";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            //System.out.println("0 row insert success!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sqls.append("insert into ").append(tableName).append(" (\"Row_Number\",\"Father_Number\",\"Level_Number\"," +
                "\"Mark_Number\",\"Reference_Designator\",\"Type\",\"Name\",\"Revision\"," +
                "\"State\",\"UOM\",\"Quantity\",\"Description\",\"Manufacturer_Equivalent_part\"," +
                "\"Manufacturer\",\"RDO\")" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
