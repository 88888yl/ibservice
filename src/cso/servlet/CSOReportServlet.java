package cso.servlet;

import cso.database.CSOReportToDB;
import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 14-12-6.
 */
public class CSOReportServlet extends HttpServlet {
    List<List<Integer>> reportList;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CSOReportToDB csoReportToDB = new CSOReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        csoReportToDB.getConnect();

        if ((reportList = csoReportToDB.getReport()) != null) {
            JSONArray jsonArray = JSONArray.fromObject(reportList);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
