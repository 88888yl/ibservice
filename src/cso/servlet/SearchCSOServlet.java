package cso.servlet;

import cso.utils.SearchByInfo;
import net.sf.json.JSONArray;
import partfinder.database.DBtoObject;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 14-11-10.
 * Study code
 */
public class SearchCSOServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String age_min = null;
        String age_max = null;
        String cso_num = null;
        String owner_name = null;
        String problem = null;

        List<List<String>> infoLists = null;

        age_min = request.getParameter("age_min");
        age_max = request.getParameter("age_max");
        cso_num = request.getParameter("csonumber");
        owner_name = request.getParameter("ownername");
        problem = request.getParameter("problem");

        SearchByInfo searchByInfo = new SearchByInfo(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        searchByInfo.setAge_min(age_min);
        searchByInfo.setAge_max(age_max);
        searchByInfo.setCsonumber(cso_num);
        searchByInfo.setOwnername(owner_name);
        searchByInfo.setProblem(problem);

        if ((infoLists = searchByInfo.infoSearch()) != null) {
            JSONArray jsonArray = JSONArray.fromObject(infoLists);
            response.getWriter().write(jsonArray.toString());
        } else {
            response.getWriter().write("fail");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
