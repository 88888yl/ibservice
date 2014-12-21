package scr.servlet;

import cso.database.CSOReportToDB;
import scr.database.SCRReportToDB;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 305028327 on 2014/12/21.
 */
public class SCRTreeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String treeStr;
        SCRReportToDB scrReportToDB = new SCRReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        scrReportToDB.getConnect();
        if ((treeStr = scrReportToDB.getTree()) != null) {
            response.getWriter().write(treeStr);
        } else {
            response.getWriter().write("Get PSI error");
        }
        scrReportToDB.closeAll();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
