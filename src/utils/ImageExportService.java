package utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageExportService extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String svg = request.getParameter("svg");
        String table = request.getParameter("table");
        String name = request.getParameter("name");

        if (email != null && !email.isEmpty()
                && svg != null && !svg.isEmpty()
                && table != null && !table.isEmpty()) {
            sendEmail(email, svg, table, name);
            response.getWriter().print("Success!");
        } else {
            response.getWriter().print("Send email failed");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void sendEmail(String emailAddr, String svg, String table, String name) {
        if (emailAddr.contains("@") && emailAddr.contains(".")) {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.163.com");
            email.setSmtpPort(25);
            email.setAuthenticator(new DefaultAuthenticator("ibservice",
                    "service123456"));
            StringBuilder sb = new StringBuilder();
            sb.append(table).append("<br>").append(svg);
            try {
                email.setFrom("ibservice@163.com");
                email.setSubject(name);
                email.setHtmlMsg(sb.toString());
                email.addTo(emailAddr);
                email.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
