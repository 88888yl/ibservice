package cso.database;

import net.sf.json.JSONArray;
import utils.GlobalVariables;
import utils.Percentile;

import java.sql.*;
import java.util.*;

/**
 * Created by myl on 14-12-4.
 */
public class CSOReportToDB {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public CSOReportToDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
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

    public void createCSOReportTable(String reportTableName) {
        String sql = "create table " + reportTableName + " (" +
                "\"Year\" NUMBER," +
                "\"FW\" NUMBER," +
                "\"Total Open CSO\" NUMBER," +
                "\"Total Closed CSO\" NUMBER," +
                "\"All Red CSO\" NUMBER," +
                "\"Open Red CSO\" NUMBER," +
                "\"Close Red CSO\" NUMBER," +
                "\"Red Open This Week\" NUMBER," +
                "\"Red Closed This Week\" NUMBER," +
                "\"P95 All Open CSO\" NUMBER," +
                "\"P95 Non Red CSO\" NUMBER," +
                "\"P95 Red CSO\" NUMBER," +
                "\"P50 Red CSO\" NUMBER," +
                "\"CSO Over 60 Days\" NUMBER," +
                "\"Red CSO Over 60 Days\" NUMBER," +
                "\"New Open This Week\" NUMBER," +
                "\"New Closed This Week\" NUMBER," +
                "\"Sum CSO\" NUMBER," +
                "\"Oldest CSO\" NUMBER" +
                ")";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void insertCSOReportData(String tableName, String reportTableName) {

        /** these maps are used for loading cso information by every year and every fw. */
        Map<String, List<String>> open_cso = new HashMap<String, List<String>>();
        Map<String, List<Integer>> open_cso_age = new HashMap<String, List<Integer>>();
        Map<String, List<Integer>> red_open_cso_age = new HashMap<String, List<Integer>>();
        Map<String, List<Integer>> non_red_open_cso_age = new HashMap<String, List<Integer>>();
        Map<String, List<String>> all_cso_red = new HashMap<String, List<String>>();
        Map<String, List<String>> open_cso_red = new HashMap<String, List<String>>();
        Map<String, List<String>> close_cso_red = new HashMap<String, List<String>>();
        Map<String, List<String>> close_cso = new HashMap<String, List<String>>();
        Map<String, String> close_sum_cso = new HashMap<String, String>();

        String open_cso_sql = String.format("select open_year,open_fw,close_year,close_fw,\"CSO Number\",\"CSO_AGE\",\"Customer Temperature\" from %s order by open_year,open_fw", tableName);
        String open_sum_sql = String.format("select open_year,open_fw,count(1) new_open from %s group by open_year,open_fw order by open_year,open_fw", tableName);
        String close_cso_sql = String.format("select close_year,close_fw,\"CSO Number\" from %s order by close_year,close_fw", tableName);
        String close_sum_sql = String.format("select close_year,close_fw,count(1) new_close from %s group by close_year,close_fw order by close_year,close_fw", tableName);

        String open_year;
        String open_fw;
        String cso_red;
        String close_year;
        String close_fw;
        String cso_year;
        String cso_fw;
        String new_open_cso;

        List<String> all_opend = new ArrayList<String>();
        List<Integer> all_opend_age = new ArrayList<Integer>();
        List<Integer> open_cso_age_sum = new ArrayList<Integer>();
        List<Integer> red_open_cso_age_sum = new ArrayList<Integer>();
        List<Integer> non_red_open_cso_age_sum = new ArrayList<Integer>();

        double p95_open_cso;
        double p95_red_open_cso;
        double p95_non_red_open_cso;
        double p50_open_cso;

        int open_sum = 0;
        int all_close = 0;
        int all_cso = 0;
        int red_cso_sum = 0;
        int open_red_cso = 0;
        int close_red_cso = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(open_cso_sql);
            while (rs.next()) {
                open_year = rs.getString("open_year");
                open_fw = rs.getString("open_fw");
                cso_red = rs.getString("Customer Temperature");

                /** open cso */
                if (open_cso.get(open_year + "," + open_fw) == null) {
                    open_cso.put(open_year + "," + open_fw, new ArrayList<String>());
                }
                open_cso.get(open_year + "," + open_fw).add(rs.getString("CSO Number"));

                /** open cso age */
                if (open_cso_age.get(open_year + "," + open_fw) == null) {
                    open_cso_age.put(open_year + "," + open_fw, new ArrayList<Integer>());
                }
                if (red_open_cso_age.get(open_year + "," + open_fw) == null) {
                    red_open_cso_age.put(open_year + "," + open_fw, new ArrayList<Integer>());
                }
                if (non_red_open_cso_age.get(open_year + "," + open_fw) == null) {
                    non_red_open_cso_age.put(open_year + "," + open_fw, new ArrayList<Integer>());
                }
                open_cso_age.get(open_year + "," + open_fw).add((int) Double.parseDouble(rs.getString("CSO_AGE")));
                if (cso_red.equals("R")) {
                    red_open_cso_age.get(open_year + "," + open_fw).add((int) Double.parseDouble(rs.getString("CSO_AGE")));
                } else {
                    non_red_open_cso_age.get(open_year + "," + open_fw).add((int) Double.parseDouble(rs.getString("CSO_AGE")));
                }

                /** red cso */
                if (all_cso_red.get(open_year + "," + open_fw) == null) {
                    all_cso_red.put(open_year + "," + open_fw, new ArrayList<String>());
                }
                if (open_cso_red.get(open_year + "," + open_fw) == null) {
                    open_cso_red.put(open_year + "," + open_fw, new ArrayList<String>());
                }
                if (close_cso_red.get(open_year + "," + open_fw) == null) {
                    close_cso_red.put(open_year + "," + open_fw, new ArrayList<String>());
                }
                if (cso_red.equals("R")) {
                    all_cso_red.get(open_year + "," + open_fw).add("Red");
                }
                if (cso_red.equals("R") && rs.getString("close_year") == null && rs.getString("close_fw") == null) {
                    open_cso_red.get(open_year + "," + open_fw).add("Red");
                }
                if (cso_red.equals("R") && rs.getString("close_year") != null && rs.getString("close_fw") != null) {
                    close_cso_red.get(open_year + "," + open_fw).add("Red");
                }

            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(close_cso_sql);
            while (rs.next()) {
                close_year = rs.getString("close_year");
                close_fw = rs.getString("close_fw");
                if (close_cso.get(close_year + "," + close_fw) == null) {
                    close_cso.put(close_year + "," + close_fw, new ArrayList<String>());
                }
                close_cso.get(close_year + "," + close_fw).add(rs.getString("CSO Number"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(close_sum_sql);
            while (rs.next()) {
                close_year = rs.getString("close_year");
                close_fw = rs.getString("close_fw");
                close_sum_cso.put(close_year + "," + close_fw, rs.getString("new_close"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(open_sum_sql);
            while (rs.next()) {
                cso_year = rs.getString("open_year");
                cso_fw = rs.getString("open_fw");
                new_open_cso = rs.getString("new_open");

                open_sum += Integer.parseInt(new_open_cso);
                all_cso += Integer.parseInt(new_open_cso);
                int close = Integer.parseInt(close_sum_cso.get(cso_year + "," + cso_fw) == null ? "0" : close_sum_cso.get(cso_year + "," + cso_fw));
                all_close += close;
                all_opend.addAll(open_cso.get(cso_year + "," + cso_fw));
                all_opend_age.addAll(open_cso_age.get(cso_year + "," + cso_fw));
                List<String> closed = close_cso.get(cso_year + "," + cso_fw);
                int close_ac = 0;
                if (closed != null) {
                    for (String s : closed) {
                        if (all_opend.remove(s)) {
                            all_opend_age.remove(s);
                            close_ac++;
                        }
                    }
                }
                open_sum -= close_ac;

                int open_red_cso_fw = 0;
                int close_red_cso_fw = 0;

                int open_cso_age_60 = 0;
                int open_cso_age_red_60 = 0;

                if (open_cso_age.get(cso_year + "," + cso_fw) != null && !open_cso_age.get(cso_year + "," + cso_fw).isEmpty()) {
                    int count_tmp = 0;
                    for (int i = 0; i < open_cso_age.get(cso_year + "," + cso_fw).size(); i++) {
                        open_cso_age_sum.add(open_cso_age.get(cso_year + "," + cso_fw).get(i));
                        if (open_cso_age.get(cso_year + "," + cso_fw).get(i) > 60) {
                            count_tmp++;
                        }
                    }
                    open_cso_age_60 = count_tmp;
                }
                if (red_open_cso_age.get(cso_year + "," + cso_fw) != null && !red_open_cso_age.get(cso_year + "," + cso_fw).isEmpty()) {
                    int count_tmp = 0;
                    for (int i = 0; i < red_open_cso_age.get(cso_year + "," + cso_fw).size(); i++) {
                        red_open_cso_age_sum.add(red_open_cso_age.get(cso_year + "," + cso_fw).get(i));
                        if (red_open_cso_age.get(cso_year + "," + cso_fw).get(i) > 60) {
                            count_tmp++;
                        }
                    }
                    open_cso_age_red_60 = count_tmp;
                }
                if (non_red_open_cso_age.get(cso_year + "," + cso_fw) != null && !non_red_open_cso_age.get(cso_year + "," + cso_fw).isEmpty()) {
                    for (int i = 0; i < non_red_open_cso_age.get(cso_year + "," + cso_fw).size(); i++) {
                        non_red_open_cso_age_sum.add(non_red_open_cso_age.get(cso_year + "," + cso_fw).get(i));
                    }
                }
                if (all_cso_red.get(cso_year + "," + cso_fw) != null && !all_cso_red.get(cso_year + "," + cso_fw).isEmpty()) {
                    red_cso_sum += all_cso_red.get(cso_year + "," + cso_fw).size();
                }
                if (open_cso_red.get(cso_year + "," + cso_fw) != null && !open_cso_red.get(cso_year + "," + cso_fw).isEmpty()) {
                    open_red_cso += open_cso_red.get(cso_year + "," + cso_fw).size();
                    open_red_cso_fw = open_cso_red.get(cso_year + "," + cso_fw).size();
                }
                if (close_cso_red.get(cso_year + "," + cso_fw) != null && !close_cso_red.get(cso_year + "," + cso_fw).isEmpty()) {
                    close_red_cso += close_cso_red.get(cso_year + "," + cso_fw).size();
                    close_red_cso_fw = close_cso_red.get(cso_year + "," + cso_fw).size();
                }

                Percentile p95_open_cso_percentile = new Percentile(open_cso_age_sum, 0.95);
                Percentile p95_red_open_cso_percentile = new Percentile(red_open_cso_age_sum, 0.95);
                Percentile p95_non_red_open_cso_percentile = new Percentile(non_red_open_cso_age_sum, 0.95);
                Percentile p50_red_open_cso_percentile = new Percentile(red_open_cso_age_sum, 0.5);
                p95_open_cso = p95_open_cso_percentile.getResult();
                p95_red_open_cso = p95_red_open_cso_percentile.getResult();
                p95_non_red_open_cso = p95_non_red_open_cso_percentile.getResult();
                p50_open_cso = p50_red_open_cso_percentile.getResult();

                String insert_sql = String.format(
                        "insert into " + reportTableName + " (" +
                                "\"Year\"," +
                                "\"FW\"," +
                                "\"New Open This Week\"," +
                                "\"New Closed This Week\"," +
                                "\"All Red CSO\"," +
                                "\"Open Red CSO\"," +
                                "\"Close Red CSO\"," +
                                "\"P95 All Open CSO\"," +
                                "\"P95 Red CSO\"," +
                                "\"P50 Red CSO\"," +
                                "\"P95 Non Red CSO\"," +
                                "\"Total Open CSO\"," +
                                "\"Total Closed CSO\"," +
                                "\"Sum CSO\"," +
                                "\"Oldest CSO\"" +
                                ") values (" +
                                "'%s','%s','%s','%d','%d','%d','%d','%s','%s','%s','%s','%d','%d','%d','%d')",
                        cso_year, cso_fw, new_open_cso, close, red_cso_sum, open_red_cso, close_red_cso, p95_open_cso,
                        p95_red_open_cso, p50_open_cso, p95_non_red_open_cso, open_sum, all_close, all_cso, Collections.max(all_opend_age));
                String insert_sql2 = String.format(
                        "update " + reportTableName + " set " +
                                "\"Red Open This Week\"=%d," +
                                "\"Red Closed This Week\"=%d," +
                                "\"CSO Over 60 Days\"=%d," +
                                "\"Red CSO Over 60 Days\"=%d " +
                                "where \"Year\"=%s and \"FW\"=%s",
                        open_red_cso_fw, close_red_cso_fw, open_cso_age_60, open_cso_age_red_60, cso_year, cso_fw);
                Statement s = con.createStatement();
                s.executeQuery(insert_sql);
                s.executeQuery(insert_sql2);
                s.close();
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<List<Integer>> getReport(String reportTableName) {
        List<List<Integer>> reportList = new ArrayList<List<Integer>>();

        String sql = "select * from " + reportTableName + " order by \"Year\",\"FW\"";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                List<Integer> rowList = new ArrayList<Integer>();
                rowList.add(rs.getInt("Year"));
                rowList.add(rs.getInt("FW"));

                rowList.add(rs.getInt("Total Open CSO"));
                rowList.add(rs.getInt("Open Red CSO"));

                rowList.add(rs.getInt("CSO Over 60 Days"));
                rowList.add(rs.getInt("Red CSO Over 60 Days"));

                rowList.add(rs.getInt("New Open This Week"));
                rowList.add(rs.getInt("Red Open This Week"));
                rowList.add(rs.getInt("New Closed This Week"));
                rowList.add(rs.getInt("Red Closed This Week"));

                rowList.add((int) rs.getDouble("P95 All Open CSO"));
                rowList.add((int) rs.getDouble("P95 Red CSO"));
                rowList.add((int) rs.getDouble("P95 Non Red CSO"));
                rowList.add((int) rs.getDouble("P50 Red CSO"));

                reportList.add(rowList);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAll();
        return reportList;
    }

    public String getTree() {
        Map<String, List<String>> treeMapList = new HashMap<String, List<String>>();

        String sql = "select \"CSO Number\",\"PSI Description\" from TOTAL_CSO";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (treeMapList.get(rs.getString("PSI Description")) == null) {
                    treeMapList.put(rs.getString("PSI Description"), new ArrayList<String>());
                }
                treeMapList.get(rs.getString("PSI Description")).add(rs.getString("CSO Number") + "@" + rs.getString("PSI Description"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (Object object : treeMapList.keySet()) {
            if (object != null) {
//                if (treeMapList.get(object.toString()).size() == 1) {
//                    String csoNumberTmp = treeMapList.get(object.toString()).get(0).split("@")[0];
//                    String psiDescriptionTmp = treeMapList.get(object.toString()).get(0).split("@")[1].replaceAll("\"", "").replaceAll("\n", "");
//                    sb.append("{\"text\": \"")
//                            .append(psiDescriptionTmp)
//                            .append("\",\"checked\": false,\"leaf\": true},");
//                } else {

                String psiDescriptionTmp = treeMapList.get(object.toString()).get(0).split("@")[1].replaceAll("\"", "").replaceAll("\n", "");

//                    sb.append("{\"text\": \"").append(object.toString().replaceAll("\"", "").replaceAll("\n", ""))
                sb.append("{\"text\": \"").append(psiDescriptionTmp)
                        .append("\",\"checked\": false,\"expanded\": false,\"children\": [");
                StringBuilder subSb = new StringBuilder();
                for (String str : treeMapList.get(object.toString())) {
                    String csoNumberTmp = str.split("@")[0];
                    subSb.append("{\"text\": \"")
//                                .append(str.replaceAll("\"", "").replaceAll("\n", ""))
                            .append(csoNumberTmp)
                            .append("\",\"checked\": false,\"leaf\": true},");
                }
                String subStrTmp = subSb.substring(0, subSb.length() - 1);
                sb.append(subStrTmp).append("]},");
//                }
            }
        }
        StringBuilder totalContent = new StringBuilder();
        totalContent.append("[").append(sb.substring(0, sb.length() - 1)).append("]");

        return totalContent.toString();
    }

    public void deleteCSOReportTable(String reportTableName) {
        String sql;
        sql = "drop table " + reportTableName;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            //System.out.println(tableName + " drop Success!");
        } catch (SQLException e) {
            //e.printStackTrace();
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

    public static void main(String[] args) {
        CSOReportToDB csoReportToDB = new CSOReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        csoReportToDB.getConnect();
        csoReportToDB.deleteCSOReportTable("cso_report");
        csoReportToDB.createCSOReportTable("cso_report");
        csoReportToDB.insertCSOReportData("total_cso", "cso_report");
        //csoReportToDB.getTree();
        csoReportToDB.closeAll();
    }
}
