package scr.utils;

import utils.GlobalVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 2014/12/15.
 */
public class SearchSCRByInfo {
    private String URL;
    private String USER;
    private String PWD;

    private String age_min;
    private String age_max;
    private String scrnumber;
    private String problem;
    private String solution;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public SearchSCRByInfo(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public List<List<String>> infoSearch() {
        List<List<String>> infoLists = new ArrayList<List<String>>();
        String tableName = GlobalVariables.totalSCRTable;
        String sql = String.format("select * from %s where 1=1", tableName);

        if (age_min != null && age_min.length() > 0) {
            sql += " and scr_age>" + age_min;
        }
        if (age_max != null && age_max.length() > 0) {
            sql += " and scr_age<" + age_max;
        }
        if (scrnumber != null && scrnumber.length() > 0) {
            sql += " and \"Number\" like '%" + scrnumber + "%'";
        }
        if (problem != null && problem.length() > 0) {
            sql += " and \"Reason For Change Request\" like '%" + problem + "%'";
        }
        if (solution != null && solution.length() > 0) {
            sql += " and \"Recommended Solution\" like '%" + solution + "%'";
        }

        getConnect();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int size = metaData.getColumnCount();

            List<String> infoTitle = new ArrayList<String>();
            for (int i = 1; i <= size; i++) {
                infoTitle.add(metaData.getColumnLabel(i));
            }
            infoLists.add(infoTitle);

            while (rs.next()) {
                List<String> infoList = new ArrayList<String>();
                for (int i = 1; i <= size; i++) {
                    if (Types.DATE == metaData.getColumnType(i)) {
                        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        infoList.add(dateFormat.format(rs.getDate(metaData.getColumnLabel(i))));
                    } else {
                        String value = rs.getString(metaData.getColumnLabel(i));
                        value = value == null ? "" : value;
                        infoList.add(value);
                    }
                }
                infoLists.add(infoList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();

        return infoLists;
    }

    private void getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public String getAge_min() {
        return age_min;
    }

    public void setAge_min(String age_min) {
        this.age_min = age_min;
    }

    public String getAge_max() {
        return age_max;
    }

    public void setAge_max(String age_max) {
        this.age_max = age_max;
    }

    public String getScrnumber() {
        return scrnumber;
    }

    public void setScrnumber(String scrnumber) {
        this.scrnumber = scrnumber;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
