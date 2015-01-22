package cso.database;

import utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by myl on 14-11-10.
 * Study code
 */
public class ImportOpenToDB {
    private static final String filePath = GlobalVariables.csoPath + "Open_CSO.xlsx";    //the path to excels

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> rows;

    public ImportOpenToDB() {
    }

    public ImportOpenToDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
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

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
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
        List<String> columnNames = getColName();
        StringBuilder sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();

        for (String columnName : columnNames) {
            subSql.append("\"").append(columnName).append("\"").append(" varchar2(500),");
        }
        String substring = subSql.substring(0, subSql.length() - 1);

        sql.append("create table open_cso (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.executeUpdate("alter table open_cso modify \"Problem Description\" varchar2 (4000)");
            stmt.close();
            System.out.println();
            System.out.println("OpenCSO --- Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertExceltoTable() {
        List<String> columnNames = getColName();
        StringBuffer sqls = new StringBuffer();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();

        for (String columnName : columnNames) {
            subSql1.append("\"").append(columnName).append("\",");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        for (int i = 0; i < columnNames.size(); i++) {
            subSql2.append("?,");
        }
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        sqls.append("insert into open_cso (").append(substring).append(") values (").append(substring2).append(")");

        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sqls.toString());
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
            System.out.println("----------------: " + rows.size() + " rows insert success!");
            pst.close();
            //con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewColumn() {

        String add_sql = "alter table open_cso add open_year NUMBER ";
        String add_sql2 = "alter table open_cso add open_fw NUMBER ";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(add_sql);
            stmt.executeQuery(add_sql2);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String time_sql = "select \"CSO Number\",\"CSO Start Date\" from open_cso order by \"CSO Start Date\"";
        List<String> time_str = new ArrayList<String>();
        List<String> number_str = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(time_sql);
            while (rs.next()) {
                time_str.add(rs.getString("CSO Start Date"));
                number_str.add(rs.getString("CSO Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String open_year;
        String open_fw;
        String open_month;
        String add_sql3;
        int flag = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = Calendar.getInstance();
        for (String aTime_str : time_str) {
            try {
                cl.setTime(sdf.parse(aTime_str.substring(0, 10)));
                open_year = Integer.toString(cl.get(Calendar.YEAR));
                open_fw = Integer.toString(cl.get(Calendar.WEEK_OF_YEAR));
                open_month = Integer.toString(cl.get(Calendar.MONTH));
                if (open_month.equals("11") && open_fw.equals("1")) {
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }
                if (open_fw.equals(Integer.toString(53))) {
                    open_fw = Integer.toString(1);
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }
                add_sql3 = String.format("update open_cso set open_cso.open_year=%s,open_cso.open_fw=%s " +
                        "where open_cso.\"CSO Number\"=\'%s\'", open_year, open_fw, number_str.get(flag));
                stmt = con.createStatement();
                stmt.executeQuery(add_sql3);
                stmt.close();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            flag++;
        }
        System.out.println("-------open year added");
        System.out.println("-------open fw added");

//        String fw;
//        for (String aTime_str : time_str) {
//            int[] month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//            int y = Integer.parseInt(aTime_str.substring(0, 4));
//            int m = Integer.parseInt(aTime_str.substring(5, 7));
//            int d = 0;
//            int w = 0;
//            for (int i = 0; i < m - 1; i++) {
//                d += month[i];
//            }
//            d += Integer.parseInt(aTime_str.substring(8, 10));
//            if (d / 7 == 0)
//                w = d / 7;
//            w = d / 7 + 1;
//            fw = Integer.toString(w);
//            add_sql3 = String.format("update open_cso set open_cso.open_year=%s,open_cso.open_fw=%s " +
//                    "where open_cso.\"CSO Number\"=\'%s\'", aTime_str.substring(0, 4), fw, number_str.get(flag));
//            try {
//                stmt = con.createStatement();
//                stmt.executeQuery(add_sql3);
//                stmt.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            flag++;
//        }
//        System.out.println("-------open year added");
//        System.out.println("-------open fw added");
    }

    public void deleteTableFromExcel() {
        String sql;
        sql = "drop table open_cso";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            //System.out.println(tableName + " drop Success!");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    private List<String> getColName() {
        ExcelLoader loader = new ExcelLoader(filePath);
        rows = loader.loadData(0);
        return rows.get(0);
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

    public static void main(String[] args) {
        ImportOpenToDB importOpenToDB = new ImportOpenToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        importOpenToDB.getConnect();
        importOpenToDB.deleteTableFromExcel();
        importOpenToDB.createTableFromExcel();
        importOpenToDB.insertExceltoTable();
        importOpenToDB.addNewColumn();
        importOpenToDB.closeAll();
    }
}
