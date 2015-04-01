package cso.database;

import utils.ExcelLoader;
import utils.GlobalVariables;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by myl
 * on 2014/12/13.
 */
public class TotalCSOtoDB {
    private static final String openFilePath = GlobalVariables.csoPath + GlobalVariables.csoTableName;
//    private static final String closeFilePath = GlobalVariables.csoPath + "CSO.xls";

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private List<List<String>> rows;

    public TotalCSOtoDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createTotalCSO() {
        List<String> columnNames = getOpenCSOColName();
        StringBuilder create_sql = new StringBuilder();
        StringBuilder subSql = new StringBuilder();

        getConnect();
        deleteTotalCSO();

        for (String columnName : columnNames) {
            subSql.append("\"").append(columnName).append("\"").append(" varchar2(2000),");
        }
        String substring = subSql.substring(0, subSql.length() - 1);

        create_sql.append("create table total_cso (").append(substring).append(")");
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(create_sql.toString());
            stmt.executeUpdate("alter table total_cso modify \"Problem Description\" varchar2 (4000)");
            stmt.close();
            System.out.println();
            System.out.println("TotalCSO --- Create Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addNewColumn();
        closeAll();
    }

    public List<String> csoCatalogue() {
        getConnect();
        List<String> result = new ArrayList<String>();
        String column_sql = "SELECT column_name FROM user_tab_columns WHERE table_name=\'TOTAL_CSO\'";
        String ColumnStr = "";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(column_sql);
            StringBuilder subColumns = new StringBuilder();
            subColumns.append("{");
            while (rs.next()) {
                String value = rs.getString("COLUMN_NAME");
                if (!value.equals("Age")) {
                    if (value.equals("CSO_AGE")) {
                        value = "Age (from)";
                        subColumns.append("\'").append(value).append("\':\"\",");
                        subColumns.append("\'").append("Age (to)").append("\':\"\",");
                    } else {
                        subColumns.append("\'").append(value == null ? "" : value.replaceAll("\'", " ")).append("\':\"\",");
                    }
                }
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

    public List<String> csoSearch(Map<String, String> stringMap) {
        if (stringMap.isEmpty()) {
            getConnect();
            List<String> result = new ArrayList<String>();
            StringBuilder subSqlBuider = new StringBuilder();
            StringBuilder ageSqlBuider = new StringBuilder();
            String search_sql;
            search_sql = "select * from \"TOTAL_CSO\"";

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
        StringBuilder ageSqlBuider = new StringBuilder();
        for (Map.Entry<String, String> str : stringMap.entrySet()) {
            if (str.getKey().equals("Age (from)")) {
                ageSqlBuider.append(" and \"CSO_AGE\">=\'").append(str.getValue()).append("\'");
            } else if (str.getKey().equals("Age (to)")) {
                ageSqlBuider.append(" and \"CSO_AGE\"<=\'").append(str.getValue()).append("\'");
            } else {
                subSqlBuider.append("upper(\"").append(str.getKey()).append("\") like \'%").append(str.getValue().toUpperCase()).append("%\' and ");
            }
        }
        String sub_sql = subSqlBuider.toString();
        String search_sql;
        if (ageSqlBuider.toString().isEmpty()) {
            search_sql = "select * from \"TOTAL_CSO\" where " + sub_sql.substring(0, sub_sql.length() - 4);
        } else {
            search_sql = "select * from \"TOTAL_CSO\" where " + sub_sql + ageSqlBuider.substring(4, ageSqlBuider.length());
        }
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

    public void insertOpenCSOtoTable() {
        List<String> columnNames = getOpenCSOColName();
        StringBuilder insert_sqls = new StringBuilder();
        StringBuilder subSql1 = new StringBuilder();
        StringBuilder subSql2 = new StringBuilder();

        getConnect();

        for (String columnName : columnNames) {
            subSql1.append("\"").append(columnName).append("\",");
        }
        String substring = subSql1.substring(0, subSql1.length() - 1);
        for (int i = 0; i < columnNames.size(); i++) {
            subSql2.append("?,");
        }
        String substring2 = subSql2.substring(0, subSql2.length() - 1);
        insert_sqls.append("insert into total_cso (").append(substring).append(") values (").append(substring2).append(")");

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
            System.out.println("OpenCSO----------------: " + rows.size() + " rows insert success!");
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeAll();
    }

//    public void insertClosedCSOtoTable() {
//        List<String> columnNames = getClosedCSOColName();
//        StringBuilder insert_sqls = new StringBuilder();
//        StringBuilder subSql1 = new StringBuilder();
//        StringBuilder subSql2 = new StringBuilder();
//
//        getConnect();
//
//        for (String columnName : columnNames) {
//            if (columnName.equals("Milestone"))
//                columnName = "Milestone Status";
//            if (columnName.equals("SR Status"))
//                columnName = "Status";
//            if (columnName.equals("Owner Last Name"))
//                columnName = "Owner Name";
//            if (columnName.equals("Root Cause"))
//                columnName = "Root Cause Family";
//            subSql1.append("\"").append(columnName).append("\",");
//        }
//        String substring = subSql1.substring(0, subSql1.length() - 1);
//        for (int i = 0; i < columnNames.size(); i++) {
//            subSql2.append("?,");
//        }
//        String substring2 = subSql2.substring(0, subSql2.length() - 1);
//        insert_sqls.append("insert into total_cso (").append(substring).append(") values (").append(substring2).append(")");
//
//        try {
//            con.setAutoCommit(false);
//            PreparedStatement pst = con.prepareStatement(insert_sqls.toString());
//            for (List<String> row : rows.subList(1, rows.size())) {
//                for (int j = 0; j < columnNames.size(); j++) {
//                    if (row.get(j).isEmpty())
//                        pst.setNull(j + 1, Types.VARCHAR);
//                    else
//                        pst.setString(j + 1, row.get(j));
//                }
//                pst.addBatch();
//            }
//            pst.executeBatch();
//            con.commit();
//            System.out.println("ClosedCSO----------------: " + rows.size() + " rows insert success!");
//            pst.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        closeAll();
//    }

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

        String time_sql = "select \"CSO Number\",\"CSO Start Date\",\"Open Date\",\"Close Date\" from total_cso";

        List<String> open_time_str = new ArrayList<String>();
        List<String> close_time_str = new ArrayList<String>();
        List<String> number_str = new ArrayList<String>();

        getConnect();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(time_sql);
            while (rs.next()) {
                if (rs.getString("CSO Start Date") == null) {
                    String open_date = rs.getString("Open Date").replace(' ', '-').replaceAll(",", "");
                    String close_date = rs.getString("Close Date").replace(' ', '-').replaceAll(",", "");
                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_cso set \"Open Date\"='%s',\"Close Date\"='%s' where \"CSO Number\"='%s'",
                            open_date, close_date, rs.getString("CSO Number")));
                    statement.close();
                    open_time_str.add(open_date);
                    close_time_str.add(close_date);
                } else {
                    String dateTmp = rs.getString("CSO Start Date").substring(0, 10);
                    SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    SimpleDateFormat d2 = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
                    dateTmp = d2.format(d1.parse(dateTmp));
                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_cso set \"Open Date\"='%s' where \"CSO Number\"='%s'",
                            dateTmp, rs.getString("CSO Number")));
                    statement.close();
                    open_time_str.add(dateTmp);
                    close_time_str.add("none");
                }
                number_str.add(rs.getString("CSO Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar open_cl = Calendar.getInstance();
        Calendar close_cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);

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

                add_open_sql = String.format("update total_cso set total_cso.open_year=%s,total_cso.open_fw=%s " +
                        "where total_cso.\"CSO Number\"=\'%s\'", open_year, open_fw, number_str.get(i));

                if (!flag) {
                    add_close_sql = String.format("update total_cso set total_cso.close_year=%s,total_cso.close_fw=%s " +
                            "where total_cso.\"CSO Number\"=\'%s\'", close_year, close_fw, number_str.get(i));
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
        csoAgeCount();

        closeAll();
    }

    private void addNewColumn() {
        String add_col_sql1 = "alter table total_cso add \"Cognos Pole\" varchar2(500)";
        String add_col_sql2 = "alter table total_cso add \"Cognos LCT\" varchar2(500)";
        String add_col_sql3 = "alter table total_cso add \"Cognos Country\" varchar2(500)";
        String add_col_sql4 = "alter table total_cso add \"Cognos Zone\" varchar2(500)";
        String add_col_sql5 = "alter table total_cso add \"Cognos Modality\" varchar2(500)";
        String add_col_sql6 = "alter table total_cso add \"Mapping Region\" varchar2(500)";
        String add_col_sql7 = "alter table total_cso add \"Mapping Group\" varchar2(500)";
        String add_col_sql8 = "alter table total_cso add \"Mapping Modality\" varchar2(500)";
        String add_col_sql9 = "alter table total_cso add \"Mapping Sub Modality\" varchar2(500)";
        String add_col_sql10 = "alter table total_cso add \"MakeCentre\" varchar2(500)";
        String add_col_sql11 = "alter table total_cso add \"Service Request Type\" varchar2(500)";

        String add_open_date_sql = "alter table total_cso add \"Open Date\" varchar2(500)";
        String add_close_date_sql = "alter table total_cso add \"Close Date\" varchar2(500)";

        String add_close_sql = "alter table total_cso add close_year NUMBER";
        String add_close_sql2 = "alter table total_cso add close_fw NUMBER";
        String add_open_sql = "alter table total_cso add open_year NUMBER";
        String add_open_sql2 = "alter table total_cso add open_fw NUMBER";

        String age_sql = "alter table total_cso add cso_age NUMBER";

        try {
            stmt = con.createStatement();

            stmt.executeQuery(add_col_sql1);
            stmt.executeQuery(add_col_sql2);
            stmt.executeQuery(add_col_sql3);
            stmt.executeQuery(add_col_sql4);
            stmt.executeQuery(add_col_sql5);
            stmt.executeQuery(add_col_sql6);
            stmt.executeQuery(add_col_sql7);
            stmt.executeQuery(add_col_sql8);
            stmt.executeQuery(add_col_sql9);
            stmt.executeQuery(add_col_sql10);
            stmt.executeQuery(add_col_sql11);

            stmt.executeQuery(add_open_date_sql);
            stmt.executeQuery(add_close_date_sql);
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

    private void csoAgeCount() {
        String find_sql = "select \"CSO Number\",\"Open Date\",\"Close Date\" from total_cso";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(find_sql);
            while (rs.next()) {
                if (rs.getString("Close Date") != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
                    Calendar open_cl = Calendar.getInstance();
                    Calendar close_cl = Calendar.getInstance();

                    open_cl.setTime(sdf.parse(rs.getString("Open Date")));
                    close_cl.setTime(sdf.parse(rs.getString("Close Date")));

                    long open_time = open_cl.getTimeInMillis();
                    long close_time = close_cl.getTimeInMillis();

                    int cso_age = Integer.parseInt(String.valueOf((close_time - open_time) / (1000 * 3600 * 24)));
                    if (cso_age == 0)
                        cso_age = 1;

                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_cso set cso_age='%s' where \"CSO Number\"='%s'",
                            cso_age, rs.getString("CSO Number")));
                    statement.close();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
                    Calendar open_cl = Calendar.getInstance();
                    Calendar now_cl = Calendar.getInstance();

                    open_cl.setTime(sdf.parse(rs.getString("Open Date")));
                    Date now = new Date();
                    String now_str = sdf.format(now);
                    now_cl.setTime(sdf.parse(now_str));

                    long open_time = open_cl.getTimeInMillis();
                    long now_time = now_cl.getTimeInMillis();

                    int cso_age = Integer.parseInt(String.valueOf((now_time - open_time) / (1000 * 3600 * 24)));
                    if (cso_age == 0)
                        cso_age = 1;

                    Statement statement = con.createStatement();
                    statement.executeQuery(String.format("UPDATE total_cso set cso_age='%s' where \"CSO Number\"='%s'",
                            cso_age, rs.getString("CSO Number")));
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
        System.out.println("cso age count success!");
    }

    private List<String> getOpenCSOColName() {
        ExcelLoader loader = new ExcelLoader(openFilePath);
        rows = loader.loadData(0);
        return rows.get(0);
    }

//    private List<String> getClosedCSOColName() {
//        ExcelLoader loader = new ExcelLoader(closeFilePath);
//        rows = loader.loadData(0);
//        return rows.get(0);
//    }

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

    private void deleteTotalCSO() {
        String delete_sql = "drop table total_cso";
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
        TotalCSOtoDB totalCSOtoDB = new TotalCSOtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        totalCSOtoDB.createTotalCSO();
        totalCSOtoDB.insertOpenCSOtoTable();
//        totalCSOtoDB.insertClosedCSOtoTable();
        totalCSOtoDB.initYearFW();
    }
}
