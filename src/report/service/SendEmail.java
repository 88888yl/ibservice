package report.service;

import cso.database.CSOReportToDB;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import report.database.CreateTmpReport;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;

import javax.servlet.ServletContext;
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

            sb.append(reportList);

            try {
                email.setFrom("ibservice@163.com");
                email.setSubject(serviceType);
                email.setHtmlMsg(sb.toString());
                email.addTo(emailAddr);
                email.send();
                System.out.println("Send to " + emailAddr + " success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private StringBuilder createTableFromReportList(List<List<Integer>> reportList) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(reportList);
//
//        return sb;
//    }
//
//    private StringBuilder createChartFromReportList(List<List<Integer>> reportList) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(reportList);
//
//        return sb;
//    }
}
