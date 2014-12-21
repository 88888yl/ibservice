package access.servlet;

import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by myl on 14-11-13.
 */
public class AccessServlet extends HttpServlet {
    private static final String filePath = GlobalVariables.ipPath + "visitors.log";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String range = null;
        range = request.getParameter("range");

        System.out.println(range);

        List<String> accessList = new ArrayList<String>();
        List<String> rangeList = new ArrayList<String>();
        List<String> infoList = new ArrayList<String>();
        List<List<String>> allLists = new ArrayList<List<String>>();

        try {
            String encoding = "UTF-8";

            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    accessList.add(lineTxt);
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("error in read");
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());

        if (range == null || range.isEmpty())
            rangeList.add("nothing to choose");
        else if (range.equals("day")) {
            for (int i = accessList.size() - 1; i >= 0; i--) {
                String line = accessList.get(i);
                if (line.substring(0, 10).equals(date.substring(0, 10)))
                    rangeList.add(line);
            }

            List<String> subList = new ArrayList<String>();
            for (String aRangeList : rangeList) {
                subList.add(aRangeList.substring(31, 40));
            }

            Set<String> uniqueSet = new HashSet<String>(subList);
            for (String temp : uniqueSet) {

                infoList.add(temp + " visit " +
                        Collections.frequency(subList, temp) + " times");
            }
        } else if (range.equals("week")) {
            int end = Integer.parseInt(date.substring(8, 10)) - 7;
            if (end < 0)
                end = 0;
            for (int i = accessList.size() - 1; i >= 0; i--) {
                String line = accessList.get(i);
                int start = Integer.parseInt(line.substring(8, 10));
                if (start > end)
                    rangeList.add(line);
            }

            List<String> subList = new ArrayList<String>();
            for (String aRangeList : rangeList) {
                subList.add(aRangeList.substring(31, 40));
            }

            Set<String> uniqueSet = new HashSet<String>(subList);
            for (String temp : uniqueSet) {

                infoList.add(temp + " visit " +
                        Collections.frequency(subList, temp) + " times");
            }
        } else if (range.equals("month")) {
            for (int i = accessList.size() - 1; i >= 0; i--) {
                String line = accessList.get(i);
                if (line.substring(0, 7).equals(date.substring(0, 7)))
                    rangeList.add(line);
            }

            List<String> subList = new ArrayList<String>();
            for (String aRangeList : rangeList) {
                subList.add(aRangeList.substring(31, 40));
            }

            Set<String> uniqueSet = new HashSet<String>(subList);
            for (String temp : uniqueSet) {

                infoList.add(temp + " visit " +
                        Collections.frequency(subList, temp) + " times");
            }
        } else if (range.equals("year")) {
            for (int i = accessList.size() - 1; i >= 0; i--) {
                String line = accessList.get(i);
                if (line.substring(0, 4).equals(date.substring(0, 4)))
                    rangeList.add(line);
            }

            List<String> subList = new ArrayList<String>();
            for (String aRangeList : rangeList) {
                subList.add(aRangeList.substring(31, 40));
            }

            Set<String> uniqueSet = new HashSet<String>(subList);
            for (String temp : uniqueSet) {

                infoList.add(temp + " visit " +
                        Collections.frequency(subList, temp) + " times");
            }
        }

        allLists.add(infoList);
        allLists.add(rangeList);

        JSONArray jsonArray = JSONArray.fromObject(allLists);
        response.getWriter().write(jsonArray.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
