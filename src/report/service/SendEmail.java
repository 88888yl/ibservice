package report.service;

import cso.database.CSOReportToDB;
import report.database.CreateTmpReport;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;

import javax.servlet.ServletContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

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
                String serviceType = subscriber.get(1).toString();
                String[] itemArray = (String[]) subscriber.get(4);
                List<List<Integer>> reportList;
                if (serviceType.equals("CSO Report")) {
                    if ((reportList = getReportList(serviceType, itemArray)) != null) {
                        System.out.println(reportList);
                    }
                } else if (serviceType.equals("SCR Report")) {
                    if ((reportList = getReportList(serviceType, itemArray)) != null) {
                        System.out.println(reportList);
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

    private List<List<Integer>> getReportList(String serviceType, String[] itemArray) {
        List<List<Integer>> reportList = null;
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
        }
        return reportList;
    }
}
