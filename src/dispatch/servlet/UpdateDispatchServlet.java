package dispatch.servlet;

import dispatch.database.ImportDBfromDispatch;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl
 * on 2015/1/26.
 */
public class UpdateDispatchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username != null && username.equals(GlobalVariables.defaultUserName)
                && password != null && password.equals(GlobalVariables.defaultPassword)) {
            ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            importDBfromDispatch.insertTables();
            response.getWriter().print("Updating...");
        } else {
            response.getWriter().print("User/Pwd error");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
