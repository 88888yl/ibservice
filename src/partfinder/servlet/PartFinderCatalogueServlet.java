package partfinder.servlet;

import net.sf.json.JSONArray;
import partfinder.database.PartFinderCatalogueFromDB;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 2015/2/6.
 */
public class PartFinderCatalogueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> result;
        PartFinderCatalogueFromDB partFinderCatalogueFromDB = new PartFinderCatalogueFromDB(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        if ((result = partFinderCatalogueFromDB.partFinderCatalogue()) != null) {
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
