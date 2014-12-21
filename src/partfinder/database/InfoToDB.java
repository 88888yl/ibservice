package partfinder.database;

import utils.GlobalVariables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by myl on 14-10-17.
 */
public class InfoToDB {
    private String URL;
    private String USER;
    private String PWD;

    private String excelPath;
    private String tableName;
    private Map<String, String> tableInfo;

    private Connection con = null;
    private Statement stmt = null;

    public InfoToDB() {
    }

    public InfoToDB(String URL, String USER, String PWD, String excelPath, String tableName) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
        this.excelPath = excelPath;
        this.tableName = tableName;
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

    public void createTableFromInfo() {
        String sql;
        sql = "CREATE TABLE " + tableName + " " +
                "(\"Report_Name\" VARCHAR(50), " +
                "\"Part_rev\" VARCHAR(50), " +
                "\"Description\" varchar(50), " +
                "\"Nbr_of_level\" varchar(20), " +
                "\"Excel_Date\" varchar(20), " +
                "\"State\" varchar(20))";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println(tableName + "     Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void infoToTable() {
        String sql = "insert into " + tableName + " (\"Report_Name\",\"Part_rev\",\"Description\",\"Nbr_of_level\",\"Excel_Date\",\"State\")" +
                " values (" +
                "\'" + tableInfo.get("Report_Name") + "\'" +
                ",\'" + tableInfo.get("Part_rev") + "\'" +
                ",\'" + tableInfo.get("Description") + "\'" +
                ",\'" + tableInfo.get("Nbr_of_level") + "\'" +
                ",\'" + tableInfo.get("Excel_Date") + "\'" +
                ",\'" + tableInfo.get("State") + "\'" +
                ")";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTableFromInfo() {
        String sql;
        sql = "drop TABLE " + tableName;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void closeAll() {
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(Map<String, String> tableInfo) {
        this.tableInfo = tableInfo;
    }
}
