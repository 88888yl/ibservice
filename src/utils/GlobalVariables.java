package utils;

/**
 * Created by myl on 14-12-2.
 * global variables for ibservice
 */

/** modify this path when deploy on a new server */
public class GlobalVariables {
    /** absolute file path */
    private static final String resourcePath = "D:\\JavaProject\\resource\\";

    /** related resource file path */
    public static final String csoPath = resourcePath + "CSO/";
    public static final String ipPath = resourcePath + "ip_info/";
    public static final String bomPath = resourcePath + "BOM/";
    public static final String scrPath = resourcePath + "SCR/";
    public static final String complaintsPath = resourcePath + "Complaints/";
    public static final String dispatchPath = resourcePath + "Dispatch/";

    /** oracle table name */
    public static final String totalCSOTable = "total_cso";
    public static final String totalSCRTable = "total_scr";

    /** login user/password */
    public static final String defaultUserName = "pgh";
    public static final String defaultPassword = "pgh800pgh";

    /** oracle connection information */
    public static final String oracleUserName = "root";
    public static final String oraclePassword = "root";
    public static final String oracleDriver = "oracle.jdbc.driver.OracleDriver";
    public static final String oracleUrl = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

    /** report chart image path url, always need to change when deploy on a new server */
    public static final String csoReportChartImageUrl = "D:\\JavaProject\\resource\\Report\\cso\\cso_chart.jpg";
    public static final String scrReportChartImageUrl = "D:\\JavaProject\\resource\\Report\\scr\\scr_chart.jpg";

    /** report ppt path url, always need to change when deploy on a new server */
    public static final String csoReportPPT = "D:\\JavaProject\\resource\\Report\\cso\\cso_ppt.pptx";
    public static final String scrReportPPT = "D:\\JavaProject\\resource\\Report\\scr\\scr_ppt.pptx";
}