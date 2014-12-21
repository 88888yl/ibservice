package partfinder.servlet;

import net.sf.json.JSONArray;
import partfinder.database.DBtoObject;
import partfinder.utils.SearchByPN;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 14-10-18.
 */
public class SearchPNServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<List<DBtoObject>> infoLists = null;
        String partnumber = request.getParameter("pn");
        String description = request.getParameter("desc");
        String mep = request.getParameter("mep");
        String rdo = request.getParameter("rdo");

        SearchByPN searchByPN = new SearchByPN(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        searchByPN.setPartNumber(partnumber);
        searchByPN.setDescription(description);
        searchByPN.setMep(mep);
        searchByPN.setRdo(rdo);

        searchByPN.getConnect();

        if ((infoLists = searchByPN.FuzzySearch()) != null) {
            JSONArray jsonArray = JSONArray.fromObject(infoLists);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
        searchByPN.closeAll();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
