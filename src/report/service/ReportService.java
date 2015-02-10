package report.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        DateFormat df = new SimpleDateFormat("HH:mm:ss");

        int delayTime = 0;
        try {
            Date sendTime = df.parse("9:00:00");
            Date curTime = df.parse(String.valueOf(new Date()).substring(11, 18));

            long diff = sendTime.getTime() - curTime.getTime();
            if (diff > 0) {
              delayTime = (int) (diff);
            } else {
                delayTime = (int) (-diff) + 24 * 60 * 60 * 1000;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        timer.schedule(new SendEmail(servletContextEvent.getServletContext()), delayTime, 24 * 60 * 60 * 1000);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        timer.cancel();
        servletContextEvent.getServletContext().log("Timer destroyed");
    }
}
