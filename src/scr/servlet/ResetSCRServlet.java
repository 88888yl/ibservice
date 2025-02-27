package scr.servlet;

import scr.database.UpdateSCR;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl on 2014/12/16.
 */
public class ResetSCRServlet extends HttpServlet {
    private String username = " ";
    private String password = " ";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        username = request.getParameter("username");
        password = request.getParameter("password");

        if (GlobalVariables.defaultUserName.equals(username) && GlobalVariables.defaultPassword.equals(password)) {
            UpdateSCR updateSCR = new UpdateSCR(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            updateSCR.resetCSO();

            response.getWriter().print("Success!");
        } else {
            response.getWriter().print("Invalid username/password!");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
