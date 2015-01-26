package dispatch.servlet;

import dispatch.database.ImportDBfromDispatch;
import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl
 * on 2015/1/26.
 */
public class DispatchSearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        List<List<String>> resultLists;
        ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if ((resultLists = importDBfromDispatch.dispatchSearch(id)) != null) {
            JSONArray jsonArray = JSONArray.fromObject(resultLists);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
