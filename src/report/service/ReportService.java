package report.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;

/**
 * Created by myl
 * on 2015/2/3.
 */
public class ReportService implements ServletContextListener {
    private Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        timer = new Timer(true);
        servletContextEvent.getServletContext().log("Start timer");
        System.out.println("Start timer");
//        timer.schedule(new SendEmail(servletContextEvent.getServletContext()), 0, 24 * 60 * 60 * 1000);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        timer.cancel();
        servletContextEvent.getServletContext().log("Timer destroyed");
    }
}
