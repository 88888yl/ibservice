package scr.database;

import utils.GlobalVariables;
import utils.Percentile;

import java.sql.*;
import java.util.*;

/**
 * Created by myl on 2014/12/15.
 */
public class SCRReportToDB {
    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public SCRReportToDB(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    public void createSCRReportTable(String reportTableName) {
        String sql = "create table " + reportTableName + " (" +
                "\"Year\" NUMBER," +
                "\"FW\" NUMBER," +
                "\"Total Open SCR\" NUMBER," +
                "\"Total Closed SCR\" NUMBER," +
//                "\"Red SCR\" NUMBER," +
                "\"P95 All Open SCR\" NUMBER," +
//                "\"P95 Non Red SCR\" NUMBER," +
//                "\"P95 Red SCR\" NUMBER," +
                "\"P50 SCR\" NUMBER," +
                "\"SCR Over 60 Days\" NUMBER," +
//                "\"Red SCR Over 60 Days\" NUMBER," +
                "\"New Open This Week\" NUMBER," +
                "\"New Closed This Week\" NUMBER," +
//                "\"Red Closed This Week\" NUMBER," +
                "\"Sum SCR\" NUMBER," +
                "\"Oldest SCR\" NUMBER" +
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
    public void insertSCRReportData(String tableName, String reportTableName) {

        /** these maps are used for loading scr information by every year and every fw. */
        Map<String, List<String>> open_scr = new HashMap<String, List<String>>();
        Map<String, List<Integer>> open_scr_age = new HashMap<String, List<Integer>>();
        Map<String, List<String>> close_scr = new HashMap<String, List<String>>();
        Map<String, String> close_sum_scr = new HashMap<String, String>();

        String open_scr_sql = String.format("select open_year,open_fw,\"Number\",scr_age from %s order by open_year,open_fw", tableName);
        String open_sum_sql = String.format("select open_year,open_fw,count(1) new_open from %s group by open_year,open_fw order by open_year,open_fw", tableName);
        String close_scr_sql = String.format("select close_year,close_fw,\"Number\" from %s order by close_year,close_fw", tableName);
        String close_sum_sql = String.format("select close_year,close_fw,count(1) new_close from %s group by close_year,close_fw order by close_year,close_fw", tableName);

        String open_year;
        String open_fw;
        String close_year;
        String close_fw;
        String scr_year;
        String scr_fw;
        String new_open_scr;

        List<Integer> open_scr_age_sum = new ArrayList<Integer>();
        List<String> all_opend = new ArrayList<String>();
        List<Integer> all_opend_age = new ArrayList<Integer>();

        double p95_open_scr;
        double p50_open_scr;

        int open_sum = 0;
        int all_close = 0;
        int all_cso = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(open_scr_sql);
            while (rs.next()) {
                open_year = rs.getString("open_year");
                open_fw = rs.getString("open_fw");

                if (open_scr.get(open_year + "," + open_fw) == null) {
                    open_scr.put(open_year + "," + open_fw, new ArrayList<String>());
                }
                if (open_scr_age.get(open_year + "," + open_fw) == null) {
                    open_scr_age.put(open_year + "," + open_fw, new ArrayList<Integer>());
                }
                open_scr.get(open_year + "," + open_fw).add(rs.getString("Number"));
                open_scr_age.get(open_year + "," + open_fw).add((int) Double.parseDouble(rs.getString("scr_age")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(close_scr_sql);
            while (rs.next()) {
                close_year = rs.getString("close_year");
                close_fw = rs.getString("close_fw");
                if (close_scr.get(close_year + "," + close_fw) == null) {
                    close_scr.put(close_year + "," + close_fw, new ArrayList<String>());
                }
                close_scr.get(close_year + "," + close_fw).add(rs.getString("Number"));
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
                close_sum_scr.put(close_year + "," + close_fw, rs.getString("new_close"));
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
                scr_year = rs.getString("open_year");
                scr_fw = rs.getString("open_fw");
                new_open_scr = rs.getString("new_open");

                open_sum += Integer.parseInt(new_open_scr);
                all_cso += Integer.parseInt(new_open_scr);
                int close = Integer.parseInt(close_sum_scr.get(scr_year + "," + scr_fw) == null ? "0" : close_sum_scr.get(scr_year + "," + scr_fw));
                all_close += close;
                all_opend.addAll(open_scr.get(scr_year + "," + scr_fw));
                all_opend_age.addAll(open_scr_age.get(scr_year + "," + scr_fw));
                List<String> closed = close_scr.get(scr_year + "," + scr_fw);
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

                int open_cso_age_60 = 0;

                if (open_scr_age.get(scr_year + "," + scr_fw) != null && !open_scr_age.get(scr_year + "," + scr_fw).isEmpty()) {
                    int count_tmp = 0;
                    for (int i = 0; i < open_scr_age.get(scr_year + "," + scr_fw).size(); i++) {
                        open_scr_age_sum.add(open_scr_age.get(scr_year + "," + scr_fw).get(i));
                        if (open_scr_age.get(scr_year + "," + scr_fw).get(i) > 60) {
                            count_tmp++;
                        }
                    }
                    open_cso_age_60 = count_tmp;
                }

                Percentile p95_open_scr_percentile = new Percentile(open_scr_age_sum, 0.95);
                Percentile p50_open_scr_percentile = new Percentile(open_scr_age_sum, 0.5);
                p95_open_scr = p95_open_scr_percentile.getResult();
                p50_open_scr = p50_open_scr_percentile.getResult();

                String insert_sql = String.format("insert into " + reportTableName + " (" +
                                "\"Year\"," +
                                "\"FW\"," +
                                "\"New Open This Week\"," +
                                "\"New Closed This Week\"," +
                                "\"Total Open SCR\"," +
                                "\"Total Closed SCR\"," +
                                "\"SCR Over 60 Days\"," +
                                "\"P95 All Open SCR\"," +
                                "\"P50 SCR\"," +
                                "\"Sum SCR\"," +
                                "\"Oldest SCR\"" +
                                ") values (" +
                                "'%s','%s','%s','%d','%d','%d','%s','%s','%s','%d','%d')",
                        scr_year, scr_fw, new_open_scr, close, open_sum, all_close, open_cso_age_60, p95_open_scr, p50_open_scr, all_cso, Collections.max(all_opend_age));
                Statement s = con.createStatement();
                s.executeQuery(insert_sql);
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

                rowList.add(rs.getInt("Total Open SCR"));

                rowList.add(rs.getInt("SCR Over 60 Days"));

                rowList.add(rs.getInt("New Open This Week"));
                rowList.add(rs.getInt("New Closed This Week"));

                rowList.add((int) rs.getDouble("P95 All Open SCR"));
                rowList.add((int) rs.getDouble("P50 SCR"));

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

        String sql = "select \"Number\",\"Item Description\" from TOTAL_SCR";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (treeMapList.get(rs.getString("Item Description")) == null) {
                    treeMapList.put(rs.getString("Item Description"), new ArrayList<String>());
                }
                treeMapList.get(rs.getString("Item Description")).add(rs.getString("Number") + "@" + rs.getString("Item Description"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (Object object : treeMapList.keySet()) {
            if (object != null) {
                String psiDescriptionTmp = treeMapList.get(object.toString()).get(0).split("@")[1].replaceAll("\"", "").replaceAll("\n", "");
                sb.append("{\"text\": \"").append(psiDescriptionTmp)
                        .append("\",\"checked\": false,\"expanded\": false,\"children\": [");
                StringBuilder subSb = new StringBuilder();
                for (String str : treeMapList.get(object.toString())) {
                    String csoNumberTmp = str.split("@")[0];
                    subSb.append("{\"text\": \"")
                            .append(csoNumberTmp)
                            .append("\",\"checked\": false,\"leaf\": true},");
                }
                String subStrTmp = subSb.substring(0, subSb.length() - 1);
                sb.append(subStrTmp).append("]},");
            }
        }
        StringBuilder totalContent = new StringBuilder();
        totalContent.append("[").append(sb.substring(0, sb.length() - 1)).append("]");

        return totalContent.toString();
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

    public void deleteSCRReportTable(String reportTableName) {
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
        SCRReportToDB scrReportToDB = new SCRReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        scrReportToDB.getConnect();
        scrReportToDB.deleteSCRReportTable("scr_report");
        scrReportToDB.createSCRReportTable("scr_report");
        scrReportToDB.insertSCRReportData("total_scr", "scr_report");
        scrReportToDB.closeAll();
    }
}
