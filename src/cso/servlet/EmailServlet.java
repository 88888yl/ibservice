package cso.servlet;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by myl on 14-12-8.
 */
public class EmailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailAddr = request.getParameter("email");
        if (emailAddr.contains("@") && emailAddr.contains(".")) {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.163.com");
            email.setSmtpPort(25);
            email.setAuthenticator(new DefaultAuthenticator("ibservice",
                    "service123456"));
            StringBuilder sb = new StringBuilder(1024);
            try {
                sb
                        .append("\t\t\t\t\t<table width=\"500\" style=\"border-collapse: collapse;\" border=\"1\"\r\n");
                sb.append("\t\t\t\t\t\tcellpadding=\"0\" cellspacing=\"0\">\r\n");
                sb.append("\t\t\t\t\t\t");

                Class.forName(GlobalVariables.oracleDriver);
                Connection conn = DriverManager.getConnection(
                        GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
                Statement stmt = conn.createStatement();
                String sql = "select * from cso_report order by \"Year\",\"FW\"";
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int size = metaData.getColumnCount();

                sb.append("\r\n");
                sb.append("\t\t\t\t\t\t<tr>\r\n");
                sb
                        .append("\t\t\t\t\t\t\t<td style='font-family:GE Inspira;font-size:12px;font-weight:bold;' align='center'>\r\n");
                sb.append("\t\t\t\t\t\t\t\tRRF QTO Metrics\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb
                        .append("\t\t\t\t\t\t\t<td style='font-family:GE Inspira;font-size:12px;font-weight:bold;' align='center'>\r\n");
                sb.append("\t\t\t\t\t\t\t\tTarget\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t\t");

                int colIndex = 1;

                List<Map> dataList = new ArrayList();
                Map dataMap = null;
                int rowIndex = 0;
                while (rs.next() && rowIndex++ < 3) {
                    dataMap = new HashMap();
                    for (int i = 1; i <= size; i++) {
                        dataMap.put(metaData.getColumnLabel(i), rs
                                .getString(metaData.getColumnLabel(i)));
                    }
                    dataList.add(dataMap);
                }
                for (int i = dataList.size() - 1; i >= 0; i--) {
                    sb
                            .append("<td style='font-family:GE Inspira;font-size:12px;font-weight:bold;' align='center'>FW"
                                    + dataList.get(i).get("FW") + "</td>");
                }

                sb.append("\r\n");
                sb.append("\t\t\t\t\t\t</tr>\r\n");
                sb.append("\t\t\t\t\t\t<tr>\r\n");
                sb
                        .append("\t\t\t\t\t\t\t<td style='font-family:GE Inspira;font-size:12px;font-weight:bold;' align='left'>\r\n");
                sb.append("\t\t\t\t\t\t\t\tCSO\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t\t<td >\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t\t<td >\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t\t<td >\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t\t<td >\r\n");
                sb.append("\t\t\t\t\t\t\t</td>\r\n");
                sb.append("\t\t\t\t\t\t</tr>\r\n");
                sb.append("\t\t\t\t\t\t");

                for (int i = 3; i < 8; i++) {
                    sb.append("<tr>");
                    String title = metaData.getColumnLabel(i);
                    // title = title.replaceAll("_","_<br>");
                    sb
                            .append("<td style='font-family:GE Inspira;font-size:12px;font-weight:bold;' align='right'>"
                                    + title + "</td>");
                    if ("P95".equals(title)) {
                        sb
                                .append("<td align='center'  style='font-family:GE Inspira;font-size:12px;'>90</td>");
                    } else {
                        sb
                                .append("<td align='center' style='font-family:GE Inspira;font-size:12px;'>-</td>");
                    }
                    for (int j = dataList.size() - 1; j >= 0; j--) {
                        sb
                                .append("<td align='center' style='font-family:GE Inspira;font-size:12px;'>"
                                        + dataList.get(j).get(title) + "</td>");
                    }
                    sb.append("</tr>");
                }

                rs.close();
                stmt.close();
                conn.close();

                sb.append("\r\n");
                sb.append("\t\t\t\t\t</table>\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                email.setFrom("ibservice@163.com");
                email.setSubject("CSO Report");
                URL url = new URL(GlobalVariables.csoReportChartImageUrl);
                String cid = email.embed(url, "chart");
                email.setHtmlMsg(sb.toString() + "<html><img src=\"cid:" + cid + "\"></html>");
                email.addTo(emailAddr);
                email.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.getWriter().print("success");
        } else if (emailAddr.isEmpty()) {
            response.getWriter().print("input empty");
        } else {
            response.getWriter().print("error format");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
