package complaints.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.GlobalVariables;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by myl on 2015/1/26.
 */
public class UploadComplaintsServlet extends HttpServlet {
    public void init(ServletConfig config) {
        ServletContext sc = config.getServletContext();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    System.out.println("excel_name-----:" + item.getFieldName());
                } else {
                    if (item.getName() != null && !item.getName().equals("")) {
                        System.out.println("Upload file-----name----:" + item.getName());
                        System.out.println("Upload file-----size----:" + item.getSize());
                        File file = new File(GlobalVariables.complaintsPath + item.getName());
                        item.write(file);
                        response.getWriter().print("Upload success.");
                    } else {
                        response.getWriter().print("No file selected.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Upload failed.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
