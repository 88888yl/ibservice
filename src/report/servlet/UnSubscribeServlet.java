package report.servlet;

import report.database.ReportServiceDB;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl
 * on 2015/2/10.
 */
public class UnSubscribeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String serviceType = request.getParameter("serviceType");
        String startTime = request.getParameter("startTime");
        String dayOfWeek = request.getParameter("dayOfWeek");
        String email = request.getParameter("email");

        ReportServiceDB reportServiceDB = new ReportServiceDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        reportServiceDB.unSubscribe(email, serviceType, startTime, dayOfWeek);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
