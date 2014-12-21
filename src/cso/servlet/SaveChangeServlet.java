package cso.servlet;

import cso.database.UpdateCSO;
import cso.database.UpdateOpenFromWeb;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 14-11-16.
 */
public class SaveChangeServlet extends HttpServlet {
    private String username = " ";
    private String password = " ";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        username = request.getParameter("username");
        password = request.getParameter("password");
        String changeResult = request.getParameter("changeResult");

        JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(changeResult);
        List<List<String>> changeResultList = (List<List<String>>) JSONSerializer.toJava(jsonArray);

        if (GlobalVariables.defaultUserName.equals(username) && GlobalVariables.defaultPassword.equals(password)) {
            UpdateCSO updateCSO = new UpdateCSO(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            updateCSO.setResultList(changeResultList);
            if (updateCSO.UpdateAllChanges() == 0) {
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
