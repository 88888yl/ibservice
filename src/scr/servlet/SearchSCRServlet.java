package scr.servlet;

import net.sf.json.JSONArray;
import scr.utils.SearchSCRByInfo;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl on 2014/12/15.
 */
public class SearchSCRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String age_min = null;
        String age_max = null;
        String scr_num = null;
        String problem = null;
        String solution = null;

        List<List<String>> infoLists = null;

        age_min = request.getParameter("age_min");
        age_max = request.getParameter("age_max");
        scr_num = request.getParameter("scrnumber");
        problem = request.getParameter("problem");
        solution = request.getParameter("solution");

        SearchSCRByInfo searchSCRByInfo = new SearchSCRByInfo(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        searchSCRByInfo.setAge_min(age_min);
        searchSCRByInfo.setAge_max(age_max);
        searchSCRByInfo.setScrnumber(scr_num);
        searchSCRByInfo.setProblem(problem);
        searchSCRByInfo.setSolution(solution);

        if ((infoLists = searchSCRByInfo.infoSearch()) != null) {
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
