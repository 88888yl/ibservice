package report.servlet;

import report.database.CreateTmpReport;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl on 2015/1/14.
 */
public class RefreshChartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String items = request.getParameter("items");
        String id = request.getParameter("id");
        String[] itemArray = items.split("@");

        CreateTmpReport createTmpReport = new CreateTmpReport(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        createTmpReport.setId(id);
        createTmpReport.setItemArray(itemArray);
        createTmpReport.initReport();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
