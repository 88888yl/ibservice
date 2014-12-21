package cso.servlet;

import cso.database.CSOReportToDB;
import utils.GlobalVariables;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by myl on 2014/12/18.
 */
public class CSOTreeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String treeStr;
        CSOReportToDB csoReportToDB = new CSOReportToDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        csoReportToDB.getConnect();
        if ((treeStr = csoReportToDB.getTree()) != null) {
            response.getWriter().write(treeStr);
        } else {
            response.getWriter().write("Get PSI error");
        }
        csoReportToDB.closeAll();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
