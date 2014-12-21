package cso.database;

import partfinder.utils.ExcelLoader;
import utils.GlobalVariables;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by myl on 14-11-10.
 * Study code
 */
public class ImportCloseToDB {
    private static final String filePath = GlobalVariables.csoPath + "Closed_CSO.xlsx";    //the path to excels

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> rows;

    public ImportCloseToDB() {
    }

    public ImportCloseToDB(String URL, String USER, String PWD) {
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

        sql.append("create table close_cso (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.executeUpdate("alter table close_cso modify \"Problem Description\" varchar2 (4000)");
            stmt.close();
            System.out.println();
            System.out.println("CloseCSO --- Create Success!");
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
        sqls.append("insert into close_cso (").append(substring).append(") values (").append(substring2).append(")");

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewColumn() {

        String add_close_sql = "alter table close_cso add close_year NUMBER";
        String add_close_sql2 = "alter table close_cso add close_fw NUMBER";
        String add_open_sql = "alter table close_cso add open_year NUMBER";
        String add_open_sql2 = "alter table close_cso add open_fw NUMBER";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(add_close_sql);
            stmt.executeQuery(add_close_sql2);
            stmt.executeQuery(add_open_sql);
            stmt.executeQuery(add_open_sql2);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String time_sql = "select \"CSO Number\",\"Close Date\" from close_cso order by \"Close Date\"";
        List<String> time_str = new ArrayList<String>();
        List<String> number_str = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(time_sql);
            while (rs.next()) {
                time_str.add(rs.getString("Close Date"));
                number_str.add(rs.getString("CSO Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String close_year;
        String close_fw;
        String close_month;
        String add_close_sql3;
        String date;
        int flag = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
        Calendar cl = Calendar.getInstance();
        for (String aTime_str : time_str) {
            try {
                date = aTime_str.replace(' ', '-').replaceAll(",", "");
                cl.setTime(sdf.parse(date));
                close_year = Integer.toString(cl.get(Calendar.YEAR));
                close_fw = Integer.toString(cl.get(Calendar.WEEK_OF_YEAR));
                close_month = Integer.toString(cl.get(Calendar.MONTH));
                if (close_month.equals("11") && close_fw.equals("1")) {
                    int tmp = Integer.parseInt(close_year) + 1;
                    close_year = Integer.toString(tmp);
                }
                if (close_fw.equals(Integer.toString(53))) {
                    close_fw = Integer.toString(1);
                    close_year = Integer.toString(cl.get(Calendar.YEAR) + 1);
                }
                add_close_sql3 = String.format("update close_cso set close_cso.close_year=%s,close_cso.close_fw=%s " +
                        "where close_cso.\"CSO Number\"=\'%s\'", close_year, close_fw, number_str.get(flag));
                stmt = con.createStatement();
                stmt.executeQuery(add_close_sql3);
                stmt.close();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            flag++;
        }
        System.out.println("-------close year added");
        System.out.println("-------close fw added");

        String open_time_sql = "select \"CSO Number\",\"Open Date\" from close_cso order by close_year, close_fw";
        List<String> open_time_str = new ArrayList<String>();
        List<String> open_number_str = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(open_time_sql);
            while (rs.next()) {
                open_time_str.add(rs.getString("Open Date"));
                open_number_str.add(rs.getString("CSO Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String open_year;
        String open_fw;
        String add_open_sql3;
        String open_month;
        String date2;
        int flag2 = 0;
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
        Calendar cl2 = Calendar.getInstance();
        for (String aTime_str : open_time_str) {
            try {
                date2 = aTime_str.replace(' ', '-').replaceAll(",", "");
                cl2.setTime(sdf2.parse(date2));
                open_year = Integer.toString(cl2.get(Calendar.YEAR));
                open_fw = Integer.toString(cl2.get(Calendar.WEEK_OF_YEAR));
                open_month = Integer.toString(cl2.get(Calendar.MONTH));
                if (open_month.equals("11") && open_fw.equals("1")) {
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }
                if (open_fw.equals(Integer.toString(53))) {
                    open_fw = Integer.toString(1);
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }
                add_open_sql3 = String.format("update close_cso set close_cso.open_year=%s,close_cso.open_fw=%s " +
                        "where close_cso.\"CSO Number\"=\'%s\'", open_year, open_fw, open_number_str.get(flag2));
                stmt = con.createStatement();
                stmt.executeQuery(add_open_sql3);
                stmt.close();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            flag2++;
        }
        System.out.println("-------open year added");
        System.out.println("-------open fw added");
    }

    public void deleteTableFromExcel() {
        String sql;
        sql = "drop table close_cso";
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
        ImportCloseToDB importCloseToDB = new ImportCloseToDB(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        importCloseToDB.getConnect();
        importCloseToDB.deleteTableFromExcel();
        importCloseToDB.createTableFromExcel();
        importCloseToDB.insertExceltoTable();
        importCloseToDB.addNewColumn();
        importCloseToDB.closeAll();
    }
}
