package access.servlet;

import net.sf.json.JSONArray;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/8.
 */
public class IPAddressInfoServlet extends HttpServlet {
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

        List<String> result = new ArrayList<String>();
        String fields = "[{name: \'IP (except localhost)\'},{name: \'Date\'},{name: \'Time\'}]";
        String columns = "[{text: \'IP (except localhost)\', sortable: false, resizable: false, width: 140, flex: 0, dataIndex: \'IP (except localhost)\'}," +
                "{text: \'Date\', sortable: false, resizable: false, width: 90, flex: 0, dataIndex: \'Date\'}," +
                "{text: \'Time\', sortable: false, resizable: false, width: 80, flex: 0, dataIndex: \'Time\'}]";
        result.add(fields);
        result.add(columns);

        StringBuilder subData = new StringBuilder();
        subData.append("[");
        for (int i = logList.size() - 1; i >= 0; i--) {
            String[] strResult = logList.get(i).split("   ------   ");
            if (!strResult[1].equals("127.0.0.1")) {
                subData.append("[\'").append(strResult[1]).append("\',\'")
                        .append(strResult[0].substring(0, 10)).append("\',\'")
                        .append(strResult[0].substring(11)).append("\'],");
            }
        }
        String data = (subData.substring(0, subData.length() - 1) + "]");
        result.add(data);

        JSONArray jsonArray = JSONArray.fromObject(result);
        response.getWriter().write(jsonArray.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
