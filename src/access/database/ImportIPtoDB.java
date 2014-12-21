package access.database;

import utils.GlobalVariables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl on 14-11-6.
 */
public class ImportIPtoDB {
    private static final String filePath = GlobalVariables.ipPath + "IP_DATA.txt";

    private List<String> ipList = null;
    private String tableName = "Global_IP_Info";
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public Connection getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void deleteTableFromIpInfo() {
        String sql;
        sql = String.format("drop TABLE %s", tableName);
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println(tableName + " drop Success!");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public void createTableFromIpInfo() {

        String sql = String.format("create table %s (FIP number(20),TIP number(20),ADDR varchar2(200))", tableName);
        System.out.println(sql);
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

    public void readIpInfoFromTxt() {
        try {
            String encoding = "UTF-8";
            ipList = new ArrayList<String>();
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    ipList.add(lineTxt);
                }
                System.out.println("------------size: " + ipList.size());
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("error in read");
            e.printStackTrace();
        }
    }

    public void insert2DB() {

        String fip_str;
        String tip_str;
        long fip, tip;

        String addr;
        StringBuffer sqls = new StringBuffer();
        sqls.append("insert into ").append(tableName).append(" (FIP,TIP,ADDR)" +
                " values (?,?,?)");
        try {
            con.setAutoCommit(false);
            int n = ipList.size();
            int j = n / 10000 + 1;
            for (int loop = 0; loop < j - 1; loop++) {
                PreparedStatement pst = con.prepareStatement(sqls.toString());
                int subLoopStart = loop * 10000;
                int subLoopEnd = (loop + 1) * 10000;
                for (int i = subLoopStart; i < subLoopEnd; i++) {
                    String line = ipList.get(i);
                    fip_str = line.substring(0, line.indexOf(" "));
                    tip_str = line.substring(16).substring(0, line.substring(16).indexOf(" "));
                    addr = line.substring(32);
                    fip = ipToLong(fip_str);
                    tip = ipToLong(tip_str);
                    pst.setLong(1, fip);
                    pst.setLong(2, tip);
                    pst.setString(3, addr);
                    pst.addBatch();
                }
                pst.executeBatch();
                con.commit();
                System.out.println("----------------: 10000 lines insert success!");
                pst.close();
            }
            PreparedStatement pst = con.prepareStatement(sqls.toString());
            for (int i = (j - 1) * 10000; i < n; i++) {
                String line = ipList.get(i);
                fip_str = line.substring(0, line.indexOf(" "));
                tip_str = line.substring(16).substring(0, line.substring(16).indexOf(" "));
                addr = line.substring(32);
                fip = ipToLong(fip_str);
                tip = ipToLong(tip_str);
                pst.setLong(1, fip);
                pst.setLong(2, tip);
                pst.setString(3, addr);
                pst.addBatch();
            }
            pst.executeBatch();
            con.commit();
            System.out.println("----------------: last lines insert success!");
            pst.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long ipToLong(String strIp) {
        long[] ip = new long[4];
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static void main(String[] args) {
        ImportIPtoDB importIPtoDB = new ImportIPtoDB();
        importIPtoDB.readIpInfoFromTxt();
        importIPtoDB.getConnect();
        importIPtoDB.deleteTableFromIpInfo();
        importIPtoDB.createTableFromIpInfo();
        importIPtoDB.insert2DB();
        importIPtoDB.closeAll();
    }
}
