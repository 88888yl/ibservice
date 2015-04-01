package complaints.servlet;

import complaints.database.ImportDBfromAllSheets;
import dispatch.database.ImportDBfromDispatch;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl on 2015/4/1.
 */
public class DeleteComplaintsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = " ";
        String password = " ";

        username = request.getParameter("username");
        password = request.getParameter("password");

        System.out.println("User: " + username + ", Pwd: " + password);
        ImportDBfromAllSheets importDBfromAllSheets = new ImportDBfromAllSheets(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if (username.equals(GlobalVariables.defaultUserName) && password.equals(GlobalVariables.defaultPassword)) {
            importDBfromAllSheets.createTablesFromSheets();
            response.getWriter().print("Delete complaints success.");
        } else {
            response.getWriter().print("Username/password is empty or error.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
