package dispatch.servlet;

import dispatch.database.ImportDBfromDispatch;
import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/11.
 */
public class DispatchReportServlet extends HttpServlet {
    List<List<String>> reportList;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchKeys = request.getParameter("searchKeys");
        String[] dispatchNumbers = searchKeys.split(",");

        ImportDBfromDispatch importDBfromDispatch = new ImportDBfromDispatch(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if ((reportList = importDBfromDispatch.getReport(dispatchNumbers)) != null) {
            JSONArray jsonArray = JSONArray.fromObject(reportList);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
