package access.utils;

import utils.GlobalVariables;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by myl on 14-11-6.
 */
public class IPtoAddr {
    private String ip_str = "127.0.0.1";
    private String addr = "localhost";

    private static final String filePath = GlobalVariables.ipPath + "visitors.log";

    private String tableName = "Global_IP_Info";
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public IPtoAddr(String ip_str) {
        this.ip_str = ip_str;
    }

    public void findAddr() {
        if (!"127.0.0.1".equals(ip_str)) {
            long ip = ipToLong(ip_str);
            getConnect();
            String sql = String.format("select * from %s where %d>FIP and %d<TIP", tableName, ip, ip);
            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    addr = rs.getString(3);
                } else {
                    addr = "unkown";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeAll();
        }
    }

    public void writeLog() {
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String info = df.format(new Date()) + "   ------   " + ip_str + "   ------   address: " + addr + "\r\n";
                FileWriter writer = new FileWriter(filePath, true);
                writer.write(info);
                writer.close();
            } else {
                System.out.println("can not find log file");
            }
        } catch (Exception e) {
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

    private Connection getConnect() {
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
}
