package scr.servlet;

import net.sf.json.JSONArray;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 2014/12/15.
 */
public class SCRReportServlet extends HttpServlet {
    List<List<Integer>> reportList;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        SCRReportToDB scrReportToDB = new SCRReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        scrReportToDB.getConnect();

        if (id != null) {
            if (id.equals("total")) {
                if ((reportList = scrReportToDB.getReport("scr_report")) != null) {
                    JSONArray jsonArray = JSONArray.fromObject(reportList);
                    response.getWriter().write(jsonArray.toString());
                } else {
                    response.getWriter().write("fail");
                }
            }
            if (id.equals("scr_report")) {
                if ((reportList = scrReportToDB.getReport("scr_report_tmp")) != null) {
                    JSONArray jsonArray = JSONArray.fromObject(reportList);
                    response.getWriter().write(jsonArray.toString());
                } else {
                    response.getWriter().write("fail");
                }
            }
            if (id.equals("scr_service")) {

            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
