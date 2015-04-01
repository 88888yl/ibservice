package scr.servlet;

import scr.database.TotalSCRtoDB;
import scr.database.UpdateSCR;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl on 2015/3/31.
 */
public class DeleteSCRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = " ";
        String password = " ";

        username = request.getParameter("username");
        password = request.getParameter("password");

        System.out.println("User: " + username + ", Pwd: " + password);
        TotalSCRtoDB totalSCRtoDB = new TotalSCRtoDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if (username.equals(GlobalVariables.defaultUserName) && password.equals(GlobalVariables.defaultPassword)) {
            totalSCRtoDB.createTotalSCR();
            response.getWriter().print("Delete scr success.");
        } else {
            response.getWriter().print("Username/password is empty or error.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
