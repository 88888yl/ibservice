package partfinder.servlet;

import partfinder.import_db.ImportBom;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by myl on 14-10-17.
 */
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = " ";
        username = request.getParameter("username");
        String password = " ";
        password = request.getParameter("password");

        ImportBom importBom = new ImportBom();

        System.out.println(username + "-----" + password);

        if (username.equals(GlobalVariables.defaultUserName) && password.equals(GlobalVariables.defaultPassword)) {
            importBom.main_core();
            response.getWriter().print("Success!");
        } else {
            response.getWriter().print("Failed!");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
