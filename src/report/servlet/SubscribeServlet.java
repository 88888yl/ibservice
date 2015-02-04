package report.servlet;

import report.database.ReportServiceDB;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by myl
 * on 2015/2/3.
 */
public class SubscribeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String items = request.getParameter("items");
        String serviceType = request.getParameter("serviceType");
        String startTime = request.getParameter("startTime");
        String dayOfWeek = request.getParameter("dayOfWeek");
        String email = request.getParameter("email");

        String[] itemArray = items.split("@");

        ReportServiceDB reportServiceDB = new ReportServiceDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        reportServiceDB.setServiceType(serviceType);
        reportServiceDB.setStartTime(startTime);
        reportServiceDB.setDayOfWeek(dayOfWeek);
        reportServiceDB.setEmail(email);
        reportServiceDB.setItemArray(itemArray);
        reportServiceDB.insertReportDB();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
