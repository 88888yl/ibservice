package access.servlet;

import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by myl
 * on 2015/2/9.
 */
public class DayAccessServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filePath = GlobalVariables.ipPath + "visitors.log";
        String encoding = "UTF-8";
        List<String> logList = new ArrayList<String>();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    logList.add(lineTxt);
                }
                read.close();
            } else {
                System.out.println("can not find file");
            }
        } catch (Exception e) {
            System.out.println("error in read");
            e.printStackTrace();
        }

        Map<String, Integer> result = new TreeMap<String, Integer>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                return obj1.compareTo(obj2);
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int size = logList.size();
        String nowStr = logList.get(size - 1).substring(0, 10);
        try {
            Date now = sdf.parse(nowStr);
            for (int i = size - 1; i >= 0; i--) {
                String strDate = logList.get(i).substring(0, 10);
                Date fDate = sdf.parse(strDate);
                long intervalMilli = now.getTime() - fDate.getTime();
                if (((int) (intervalMilli / (24 * 60 * 60 * 1000))) <= 30) {
                    if (result.get(strDate) == null) {
                        result.put(strDate, 1);
                    } else {
                        int times = result.get(strDate) + 1;
                        result.put(strDate, times);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (result.isEmpty()) {
            response.getWriter().write("fail");
        } else {
            List<List<String>> resultLists = new ArrayList<List<String>>();
            Set<Map.Entry<String, Integer>> mySet = result.entrySet();
            for (Map.Entry<String, Integer> entry : mySet) {
                List<String> rowList = new ArrayList<String>();
                rowList.add(entry.getKey());
                rowList.add(entry.getValue().toString());
                resultLists.add(rowList);
            }
            JSONArray jsonArray = JSONArray.fromObject(resultLists);
            response.getWriter().write(jsonArray.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
