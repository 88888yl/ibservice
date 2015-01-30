package complaints.servlet;

import complaints.database.ImportDBfromAllSheets;
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
 * Created by myl on 2015/1/30.
 */
public class ComplaintsCatalogueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> result;
        ImportDBfromAllSheets importDBfromAllSheets = new ImportDBfromAllSheets(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if ((result = importDBfromAllSheets.complaintsCatalogue()) != null) {
            JSONArray jsonArray = JSONArray.fromObject(result);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
