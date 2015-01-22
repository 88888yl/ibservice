package scr.database;

import utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by myl on 2014/12/14.
 */
public class TotalSCRtoDB {
    private String scrFilePath = GlobalVariables.scrPath + "SCR.xlsx";

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> rows;

    public TotalSCRtoDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createTotalSCR() {
        List<String> columnNames = getSCRColName();
        StringBuilder create_sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();

        getConnect();
        deleteTotalSCR();

        for (String columnName : columnNames) {
            if (columnName.length() > 30) {
                columnName = columnName.substring(columnName.lastIndexOf('.') + 1, columnName.length());
            }
            if (columnName.length() > 30) {
                columnName = columnName.substring(0, columnName.indexOf(' '));
            }
            subSql.append("\"").append(columnName).append("\"").append(" varchar2(1000),");
        }
        String substring = subSql.substring(0, subSql.length() - 1);

        create_sql.append("create table total_scr (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql.toString());
            stmt.executeUpdate("alter table total_scr modify \"Reason For Change Request\" varchar2 (4000)");
            stmt.executeUpdate("alter table total_scr modify \"Recommended Solution\" varchar2 (4000)");
            stmt.close();
            System.out.println();
            System.out.println("TotalSCR --- Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addNewColumn();
        closeAll();
    }

    public void insertSCRtoTable() {
        List<String> columnNames = getSCRColName();
        StringBuilder insert_sqls = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();

        getConnect();

        for (String columnName : columnNames) {
            if (columnName.length() > 30) {
                columnName = columnName.substring(columnName.lastIndexOf('.') + 1, columnName.length());
            }
            if (columnName.length() > 30) {
                columnName = columnName.substring(0, columnName.indexOf(' '));
            }
            subSql1.append("\"").append(columnName).append("\",");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        for (int i = 0; i < columnNames.size(); i++) {
            subSql2.append("?,");
        }
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        insert_sqls.append("insert into total_scr (").append(substring).append(") values (").append(substring2).append(")");

        try {
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(insert_sqls.toString());
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
            System.out.println("SCR----------------: " + rows.size() + " rows insert success!");
            pst.close();

            stmt = con.createStatement();
            stmt.executeQuery("DELETE FROM TOTAL_SCR where TOTAL_SCR.\"Affected Items.Item Number\" not in " +
                    "(select min(\"Affected Items.Item Number\") as \"Affected Items.Item Number\" from TOTAL_SCR group by \"Number\")");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
    }

    @SuppressWarnings("ConstantConditions")
    public void initYearFW() {
        String close_year = null;
        String close_fw = null;
        String close_month = null;
        String add_close_sql = null;
        String open_year;
        String open_fw;
        String open_month;
        String add_open_sql;

        boolean flag = false;

        String time_sql = "select \"Number\",\"Originated Date\",\"Final Complete Date\" from total_scr";

        List<String> open_time_str = new ArrayList<String>();
        List<String> close_time_str = new ArrayList<String>();
        List<String> number_str = new ArrayList<String>();

        getConnect();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(time_sql);
            while (rs.next()) {
                String open_date = rs.getString("Originated Date").substring(0, 10);
                String close_date;
                if (rs.getString("Final Complete Date") == null) {
                    close_date = "none";
                } else {
                    close_date = rs.getString("Final Complete Date").substring(0, 10);
                }
                open_time_str.add(open_date);
                close_time_str.add(close_date);
                number_str.add(rs.getString("Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Calendar open_cl = Calendar.getInstance();
        Calendar close_cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        for (int i = 0; i < number_str.size(); i++) {
            try {
                open_cl.setTime(sdf.parse(open_time_str.get(i)));
                open_year = Integer.toString(open_cl.get(Calendar.YEAR));
                open_fw = Integer.toString(open_cl.get(Calendar.WEEK_OF_YEAR));
                open_month = Integer.toString(open_cl.get(Calendar.MONTH));

                if (close_time_str.get(i).equals("none")) {
                    flag = true;
                } else {
                    close_cl.setTime(sdf.parse(close_time_str.get(i)));
                    close_year = Integer.toString(close_cl.get(Calendar.YEAR));
                    close_fw = Integer.toString(close_cl.get(Calendar.WEEK_OF_YEAR));
                    close_month = Integer.toString(close_cl.get(Calendar.MONTH));
                }

                if (open_month.equals("11") && open_fw.equals("1")) {
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }
                if (open_fw.equals(Integer.toString(53))) {
                    open_fw = Integer.toString(1);
                    int tmp = Integer.parseInt(open_year) + 1;
                    open_year = Integer.toString(tmp);
                }

                if (!flag) {
                    if (close_month.equals("11") && close_fw.equals("1")) {
                        int tmp = Integer.parseInt(close_year) + 1;
                        close_year = Integer.toString(tmp);
                    }
                    if (close_fw.equals(Integer.toString(53))) {
                        close_fw = Integer.toString(1);
                        close_year = Integer.toString(close_cl.get(Calendar.YEAR) + 1);
                    }
                }

                add_open_sql = String.format("update total_scr set total_scr.open_year=%s,total_scr.open_fw=%s " +
                        "where total_scr.\"Number\"=\'%s\'", open_year, open_fw, number_str.get(i));

                if (!flag) {
                    add_close_sql = String.format("update total_scr set total_scr.close_year=%s,total_scr.close_fw=%s " +
                            "where total_scr.\"Number\"=\'%s\'", close_year, close_fw, number_str.get(i));
                }

                stmt = con.createStatement();
                stmt.executeQuery(add_open_sql);
                if (!flag) {
                    stmt.executeQuery(add_close_sql);
                }
                stmt.close();

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            flag = false;
        }
        System.out.println("------: init year and fw success!");
        scrAgeCount();
        closeAll();
    }

    private void scrAgeCount() {
        String find_sql = "select \"Number\",\"Originated Date\",\"Final Complete Date\" from total_scr";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(find_sql);
            while (rs.next()) {
                if (rs.getString("Final Complete Date") != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Calendar open_cl = Calendar.getInstance();
                    Calendar close_cl = Calendar.getInstance();

                    open_cl.setTime(sdf.parse(rs.getString("Originated Date").substring(0, 10)));
                    close_cl.setTime(sdf.parse(rs.getString("Final Complete Date").substring(0, 10)));

                    long open_time = open_cl.getTimeInMillis();
                    long close_time = close_cl.getTimeInMillis();

                    int scr_age = Integer.parseInt(String.valueOf((close_time - open_time) / (1000 * 3600 * 24)));
                    if (scr_age == 0)
                        scr_age = 1;

                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_scr set scr_age='%s' where \"Number\"='%s'",
                            scr_age, rs.getString("Number")));
                    statement.close();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Calendar open_cl = Calendar.getInstance();
                    Calendar now_cl = Calendar.getInstance();

                    open_cl.setTime(sdf.parse(rs.getString("Originated Date").substring(0, 10)));
                    java.util.Date now = new java.util.Date();
                    String now_str = sdf.format(now);
                    now_cl.setTime(sdf.parse(now_str));

                    long open_time = open_cl.getTimeInMillis();
                    long now_time = now_cl.getTimeInMillis();

                    int scr_age = Integer.parseInt(String.valueOf((now_time - open_time) / (1000 * 3600 * 24)));
                    if (scr_age == 0)
                        scr_age = 1;

                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_scr set scr_age='%s' where \"Number\"='%s'",
                            scr_age, rs.getString("Number")));
                    statement.close();
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("scr age count success!");
    }

    private List<String> getSCRColName() {
        ExcelLoader loader = new ExcelLoader(scrFilePath);
        rows = loader.loadData(0);
        return rows.get(0);
    }

    private void addNewColumn() {
        String add_close_sql = "alter table total_scr add close_year NUMBER";
        String add_close_sql2 = "alter table total_scr add close_fw NUMBER";
        String add_open_sql = "alter table total_scr add open_year NUMBER";
        String add_open_sql2 = "alter table total_scr add open_fw NUMBER";

        String age_sql = "alter table total_scr add scr_age NUMBER";

        try {
            stmt = con.createStatement();

            stmt.executeQuery(add_close_sql);
            stmt.executeQuery(add_close_sql2);
            stmt.executeQuery(add_open_sql);
            stmt.executeQuery(add_open_sql2);

            stmt.executeQuery(age_sql);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void deleteTotalSCR() {
        String delete_sql = "drop table total_scr";
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
        TotalSCRtoDB totalSCRtoDB = new TotalSCRtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        totalSCRtoDB.createTotalSCR();
        totalSCRtoDB.insertSCRtoTable();
        totalSCRtoDB.initYearFW();
    }
}
