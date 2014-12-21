package scr.servlet;

import cso.database.UpdateCSO;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import scr.database.UpdateSCR;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 2014/12/16.
 */
public class SaveSCRChangesServlet extends HttpServlet {
    private String username = " ";
    private String password = " ";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        username = request.getParameter("username");
        password = request.getParameter("password");
        String changeResult = request.getParameter("changeResult");

        JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(changeResult);
        List<List<String>> changeResultList = (List<List<String>>) JSONSerializer.toJava(jsonArray);

        if (GlobalVariables.defaultUserName.equals(username) && GlobalVariables.defaultPassword.equals(password)) {
            UpdateSCR updateSCR = new UpdateSCR(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            updateSCR.setResultList(changeResultList);
            if (updateSCR.UpdateAllChanges() == 0) {
                response.getWriter().print("Success!");
            } else {
                response.getWriter().print("Update error!");
            }
        } else {
            response.getWriter().print("Invalid username/password!");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
