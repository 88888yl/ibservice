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
 * Created by 305028327
 * on 2015/2/12.
 */
public class DispatchSysServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sysId = request.getParameter("sysId");

        if (sysId  != null  || sysId.isEmpty())  {
            List<String> result;
            ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
                    GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            if ((result = importDBfromDispatch.dispatchSysSearch(sysId)) != null) {
                JSONArray jsonArray = JSONArray.fromObject(result);
                response.getWriter().write(jsonArray.toString());
            } else {
                response.getWriter().write("fail");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
