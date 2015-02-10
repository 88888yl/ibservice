package report.service;

import cso.database.CSOReportToDB;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import report.database.CreateTmpReport;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;


import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by myl
 * on 2015/2/3.
 */
public class SendEmail extends TimerTask {
    private ServletContext context = null;

    public SendEmail(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        Date dt = new Date();
        String dayOfWeek = getWeekOfDate(dt);

        SubscriberInfo subscriberInfo = new SubscriberInfo(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        List<List<Object>> subscriberList = subscriberInfo.getInfoList(dayOfWeek);

        if (!subscriberList.isEmpty()) {
            for (List<Object> subscriber : subscriberList) {
                String email = subscriber.get(0).toString();
                String serviceType = subscriber.get(1).toString();
                String startTime = subscriber.get(2).toString();
                String[] itemArray = (String[]) subscriber.get(4);

                List<List<Integer>> reportList;
                if (serviceType.equals("CSO Report")) {
                    if ((reportList = getReportList(serviceType, itemArray, startTime)) != null) {
                        sendReport(email, serviceType, reportList);
                    }
                } else if (serviceType.equals("SCR Report")) {
                    if ((reportList = getReportList(serviceType, itemArray, startTime)) != null) {
                        sendReport(email, serviceType, reportList);
                    }
                }
            }
        }
    }

    private String getWeekOfDate(Date dt) {
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    private List<List<Integer>> getReportList(String serviceType, String[] itemArray, String startTime) {
        List<List<Integer>> reportList;
        List<List<Integer>> finalList = null;
        if (serviceType.equals("CSO Report")) {
            CreateTmpReport createTmpReport = new CreateTmpReport(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            createTmpReport.setId("cso_report");
            createTmpReport.setItemArray(itemArray);
            createTmpReport.initReport();
            CSOReportToDB csoReportToDB = new CSOReportToDB(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            csoReportToDB.getConnect();
            reportList = csoReportToDB.getReport("cso_report_tmp");
            finalList = changeList(reportList, startTime);
        } else if (serviceType.equals("SCR Report")) {
            CreateTmpReport createTmpReport = new CreateTmpReport(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            createTmpReport.setId("scr_report");
            createTmpReport.setItemArray(itemArray);
            createTmpReport.initReport();
            SCRReportToDB scrReportToDB = new SCRReportToDB(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            scrReportToDB.getConnect();
            reportList = scrReportToDB.getReport("scr_report_tmp");
            finalList = changeList(reportList, startTime);
        }
        return finalList;
    }

    private List<List<Integer>> changeList(List<List<Integer>> reportList, String startTime) {
        int startYear = Integer.parseInt(startTime.substring(0, 4));
        int startFW = Integer.parseInt(startTime.substring(7));

        Calendar calendar = Calendar.getInstance();
        int endYear = calendar.get(Calendar.YEAR);
        int endFW = calendar.get(Calendar.WEEK_OF_YEAR);

        List<List<Integer>> finalList = new ArrayList<List<Integer>>();

        for (List<Integer> fwList : reportList) {
            if (startYear == endYear) {
                if (fwList.get(0) == startYear) {
                    if (startFW <= fwList.get(1)) {
                        if (fwList.get(1) <= endFW) {
                            finalList.add(fwList);
                        }
                    }
                }
            } else {
                if (fwList.get(0) > startYear) {
                    if (fwList.get(0) < endYear) {
                        finalList.add(fwList);
                    }
                    if (fwList.get(0) == endYear) {
                        if (fwList.get(0) <= endFW) {
                            finalList.add(fwList);
                        }
                    }
                } else if (fwList.get(0) == startYear) {
                    if (fwList.get(1) >= startFW) {
                        finalList.add(fwList);
                    }
                }
            }
        }
        return finalList;
    }

    private void sendReport(String emailAddr, String serviceType, List<List<Integer>> reportList) {
        if (emailAddr.contains("@") && emailAddr.contains(".")) {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.163.com");
            email.setSmtpPort(25);
            email.setAuthenticator(new DefaultAuthenticator("ibservice", "service123456"));
            StringBuilder sb = new StringBuilder();

            sb.append(createTableFromReportList(reportList, serviceType));
            createChartFromReportList(reportList, serviceType);
            createPPTFromTableList(reportList, serviceType);
            try {
                email.setFrom("ibservice@163.com");
                email.setSubject(serviceType);
                File img, ppt;
                if (serviceType.equals("CSO Report")) {
                    img = new File(GlobalVariables.csoReportChartImageUrl);
                    ppt = new File(GlobalVariables.csoReportPPT);
                } else {
                    img = new File(GlobalVariables.scrReportChartImageUrl);
                    ppt = new File(GlobalVariables.scrReportPPT);
                }
                String cid = email.embed(img);
                email.setHtmlMsg(sb.toString() + "<html><img src=\"cid:" + cid + "\"></html>");
                email.attach(ppt);
                email.addTo(emailAddr);
                email.send();
                System.out.println("Send to " + emailAddr + " success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuilder createTableFromReportList(List<List<Integer>> reportList, String serviceType) {
        int size = reportList.size();
        List<List<Integer>> tableList;
        if (size > 12) {
            tableList = reportList.subList(reportList.size() - 12, reportList.size());
        } else {
            tableList = reportList;
        }

        StringBuilder sb = new StringBuilder();
        if (serviceType.equals("CSO Report")) {
            sb.append(createCSOTable(tableList));
        } else {
            sb.append(createSCRTable(tableList));
        }
        return sb;
    }

    private void createChartFromReportList(List<List<Integer>> reportList, String serviceType) {
        int max = 0;
        for (List<Integer> aReportList : reportList) {
            for (int j = 0; j < aReportList.size(); j++) {
                if (j == 3 || j == 4 || j == 5 || j == 6 || j == 8) {
                    max = max < aReportList.get(j) ? aReportList.get(j) : max;
                }
            }
        }
        max = (int) Math.ceil(max * 1.25);
        CreateChart createChart = new CreateChart(reportList, max, serviceType);
        try {
            createChart.print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder createCSOTable(List<List<Integer>> tableList) {
        StringBuilder sb = new StringBuilder();

        sb.append("<table align=\"center\" id=\"tblContent\" style=\"width: 100%; height: 15%;\">");

        sb.append("<tr style=\"background-color: rgb(71, 146, 200);\">")
                .append("<td style=\"text-align: center; color: rgb(255, 255, 255);\">RRF QTO Metrics</td>")
                .append("<td style=\"width: 80px; text-align: center; color: rgb(255, 255, 255);\">GOAL</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 100px; color: rgb(255, 255, 255); text-align: center;\">FW")
                    .append(aTableList.get(1)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># All Open CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(2)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># Red CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(3)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># CSO's &gt;60 Days</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(4)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># Red CSO's &gt;60 Days</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(5)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">New Open this week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(6)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">Red Open This Week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(7)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">New Closed This Week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(8)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">Red Closed This Week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(9)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P95 All Open CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(10)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P95 Red CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(11)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P95 Non Red CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(12)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P50 Red CSO</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(13)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("</table>");

        return sb;
    }

    private StringBuilder createSCRTable(List<List<Integer>> tableList) {
        StringBuilder sb = new StringBuilder();

        sb.append("<table align=\"center\" id=\"tblContent\" style=\"width: 100%; height: 15%;\">");

        sb.append("<tr style=\"background-color: rgb(71, 146, 200);\">")
                .append("<td style=\"text-align: center; color: rgb(255, 255, 255);\">RRF QTO Metrics</td>")
                .append("<td style=\"width: 80px; text-align: center; color: rgb(255, 255, 255);\">GOAL</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 100px; color: rgb(255, 255, 255); text-align: center;\">FW")
                    .append(aTableList.get(1)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># All Open SCR</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(2)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\"># SCR's &gt;60 Days</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(3)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">New Open This Week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(4)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">New Closed This Week</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(5)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P95 All Open SCR</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(6)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("<tr style=\"background-color: rgb(255, 255, 255);\">")
                .append("<td nowrap=\"\" style=\"text-align: right;\">P50 SCR</td>")
                .append("<td style=\"width: 100px; text-align: right;\">0</td>");
        for (List<Integer> aTableList : tableList) {
            sb.append("<td style=\"width: 80px; text-align: right;\">")
                    .append(aTableList.get(7)).append("</td>");
        }
        sb.append("</tr>");

        sb.append("</table>");

        return sb;
    }

    private void createPPTFromTableList(List<List<Integer>> tableList, String serviceType) {
        CreatePPT createPPT = new CreatePPT();
        if (serviceType.equals("CSO Report")) {
            createPPT.initCSO(tableList);
        } else {
            createPPT.initSCR(tableList);
        }
    }
}
