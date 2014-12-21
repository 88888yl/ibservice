package utils;

/**
 * Created by myl on 14-12-2.
 * global variables for ibservice
 */
public class GlobalVariables {
    /** modify this path when deploy on a new server */
    /** absolute file path */
    //private static final String resourcePath = "/home/myl/Desktop/ibservice_bak/resource/";
    private static final String resourcePath = "D:\\JavaProject\\resource\\";
    //private static final String resourcePath = "D:\\workspace\\resource\\";

    /** related resource file path */
    public static final String csoPath = resourcePath + "CSO/";
    public static final String ipPath = resourcePath + "ip_info/";
    public static final String bomPath = resourcePath + "BOM/";
    public static final String scrPath = resourcePath + "SCR/";

    /** oracle table name */
    public static final String totalCSOTable = "total_cso";
    public static final String openCSOTable = "open_cso";
    public static final String closeCSOTable = "close_cso";
    public static final String totalSCRTable = "total_scr";

    /** login user/password */
    public static final String defaultUserName = "pgh";
    public static final String defaultPassword = "pgh800pgh";

    /** oracle connection information */
    public static final String oracleUserName = "root";
    public static final String oraclePassword = "root";
    public static final String oracleDriver = "oracle.jdbc.driver.OracleDriver";
    public static final String oracleUrl = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

    /** cso report chart image path url, always need to change when deploy on a new server */
    public static final String csoReportChartImageUrl = "http://222.18.159.85:8080/image/cso_chart/chart.png";
    public static final String scrReportChartImageUrl = "http://222.18.159.85:8080/image/scr_chart/chart.png";
}