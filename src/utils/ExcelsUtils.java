package utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myl
 * on 2015/1/22.
 */
public class ExcelsUtils {
    /**
     * the column template
     */
    private String[] CSO_Column = {
            "Modality",
            "Activity Description",
            "Activity Owner",
            "Activity Due Date",
            "Priority",
            "Age",
            "Milestone Status",
            "Current Milestone Age",
            "CSO Number",
            "Customer Name",
            "Problem Description",
            "T",
            "Owner Name",
            "DOS",
            "ID or S/N",
            "PSI Description",
            "Customer Temperature",
            "Pole",
            "Country",
            "Region",
            "LCT",
            "Part Number",
            "Part Order Number",
            "Promise Dt",
            "Act Ship Date",
            "PQR PSR Required",
            "PQR PSR Number",
            "Iteration",
            "M1 Days Aged",
            "M2 Days Aged",
            "M3 Days Aged",
            "M4 Days Aged",
            "M5 Days Aged",
            "Ageing",
            "Grp_number",
            "Lead FSE",
            "Sub Modality",
            "System Status",
            "Make Center(Ultrasound)",
            "Product Information",
            "Levels",
            "Status",
            "CSO Owner SSO",
            "Product",
            "SR Type",
            "Serial Number",
            "Ship To Site ID",
            "Problem Type",
            "Siebel Modality",
            "Multiple Systems Involved",
            "Executive Escalation",
            "Legal Entity",
            "Who Declined PQR PSR",
            "Ship To City",
            "State",
            "Root Cause Family",
            "Root Cause Description",
            "PSI Code",
            "Operating Unit",
            "CSO Creator",
            "SR Open Date",
            "CSO Start Date",
            "OLE",
            "Country/Zone",
            "EMEA HCIT Modality",
            "EMEA HCIT SubModality",
            "Latest Update",
            "Next Action",
            "Action Owner",
            "Commented Date"
    };
    private String[] SCR_Column = {
            "Number",
            "Status",
            "Specific Supplier Request Information.Priority",
            "Originated Date",
            "Final Complete Date",
            "Reason For Change Request",
            "Recommended Solution",
            "System Generated Information.Supplier",
            "BPOC",
            "Specific Supplier Request Information.SCR Assigned To",
            "System Generated Information.Modality P and L",
            "System Generated Information.Business",
            "System Generated Information.Business Segment",
            "Affected Items.RDO",
            "Affected Items.GE Purchase Part Number",
            "Affected Items.Item Number",
            "Affected Items.Item Description",
            "Specific Supplier Request Information.GEHC Fulfillment Impact Date from MTL",
            "Specific Supplier Request Information.Fulfillment Risk",
            "System Generated Information.Last Submit Date"
    };

    private String[] PartFinder_Column = {
            "Row_Number",
            "Father_Number",
            "Level_Number",
            "Mark_Number",
            "Reference_Designator",
            "Type",
            "Name",
            "Revision",
            "State",
            "UOM",
            "Quantity",
            "Description",
            "Manufacturer_Equivalent_part",
            "Manufacturer",
            "RDO",
            "Essential_To"
    };
    private String[] Dispatch_Column = {
            "Pole Code",
            "Region",
            "Country Name",
            "Zone Name",
            "Lct Name",
            "Area Name",
            "Loc Name",
            "Customer Local Number",
            "Customer Name",
            "Lct Code",
            "Parent Modality",
            "Child Modality",
            "Grand Child Modality",
            "Psi Code",
            "Psi Description",
            "Part Local Id",
            "Non-T3 Part ID",
            "Part Source",
            "Part Description",
            "Cost Category Name",
            "Cost Sub Category Name",
            "Cost Sub Category Group",
            "Dispatch Number",
            "Dispatches",
            "Process_Date_Time",
            "Process Date",
            "Fe Number",
            "Fe Name",
            "Total Labor Hours",
            "Total Labor Cost",
            "Serial Number",
            "Parts Used Quantity",
            "Material Cost",
            "Ge Cost",
            "Total Labor and Material Cost",
            "Total Labor and GE Material Cost",
            "Service Comments",
            "Sited System Local Identifier",
            "Sited System Description",
            "Fiscal Year",
            "Fiscal Quarter",
            "Fiscal Month",
            "Fiscal Week",
            "Subsystem",
            "Subsystem - 1",
            "Subsystem - 2",
            "Subsystem - 3",
            "Subsystem - 4",
            "Subsystem - 5",
            "Consolidated Install Date",
            "Install Date",
            "Install Year",
            "Install Month",
            "Call Date",
            "System Age in days(call - install)",
            "System Age in months(call - install)",
            "System Age in months(latest call - install)",
            "Warranty Start Date",
            "Service Start Date",
            "Service Complete Date",
            "Time to Repair",
            "Service Perf System Component",
            "Subsystem_",
            "Travel Labor Hours",
            "Travel Labor Cost",
            "Cost Category Identifier",
            "MMICV",
            "Quantity",
            "Order Number",
            "Org System Status Code",
            "VALID_IB_STATUS",
            "Symptom",
            "DISPATCH_GROUP_NUMBER",
            "DISPATCH_GROUPS",
            "USN",
            "CONTRACT_DATE",
            "CREATED_BY",
            "CREATED_DATE",
            "SPAT_ID",
            "ALIAS_PART_ID",
            "SYSTEM_LIFE"
    };
    private String[] Complaints_Column = {
            "PR ID",
            "Product",
            "GE Knowledge Date",
            "Comply code",
            "Hazard.",
            "Hazard - Lower Level.",
            "Hazardous Situation.",
            "SPCR Root Cause Code",
            "Hazardous or Potentially Haz?",
            "Regulatory Non-Compliance?",
            "Modality Segment",
            "Modality",
            "Product Name",
            "Product Line",
            "Complaint Closure Code",
            "Customer Country",
            "RAC Product Line",
            "RAC Product Name",
            "SPC - System/Component Code",
            "SPC - Problem Code",
            "SPC - Correction Code",
            "Further Investigation/Actions?",
            "Additional Reportability Info",
            "Subsystem",
            "Symptom Code",
            "Problem Code",
            "Investigation Code",
            "Division",
            "PR State (AKA ¡°State¡±)",
            "Date Created",
            "Date Closed",
            "Device Identification Number",
            "Complaint / Inv / CAPA Link",
            "Customer''s Issue Description",
            "FE''s Issue Desc (Editable)",
            "Actions Taken/Rep''s (Editable)",
            "Closure Summary",
            "Comments",
            "Additional Information Requested",
            "Parts Used",
            "Software Version",
            "Manufacturing Site",
            "Model Number",
            "SPCR Symptom Description",
            "SPCR Problem Description",
            "SPCR Resolution"
    };

    private String URL;
    private String USER;
    private String PWD;

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public ExcelsUtils() {
    }

    public ExcelsUtils(String URL, String USER, String PWD) {
        this.URL = URL;
        this.USER = USER;
        this.PWD = PWD;
    }

    /**
     * @param path excels store path, not contain excel name.
     *             the only legal format of excel is end with "xlsx" or "xls".
     */
    public String[] getExcelsName(String path) {
        File file = new File(path);
        return file.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".xlsx") || name.endsWith(".xls"));
            }
        });
    }

    /**
     * @param path     excels store path, not contain excel name.
     * @param excel    name of excel where receive from one of getExcelsName().
     * @param sheetNum sheet number of the excel, default is 0.
     */
    public List<String> getColumnNames(String path, String excel, int sheetNum) {
        List<String> columnNames = new ArrayList<String>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(path + excel);
            Workbook workbook = WorkbookFactory.create(inputStream);
            int maxSheetNumber = workbook.getNumberOfSheets() - 1;
            if (sheetNum < 0) {
                System.out.println("illegal sheetNum");
                return null;
            }
            if (sheetNum > maxSheetNumber)
                sheetNum = 0;
            Sheet sheet = workbook.getSheetAt(sheetNum);
            for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                Cell cell = sheet.getRow(0).getCell(j);
                String value = getCellValue(cell);
                if (value == null)
                    value = "EmptyColumnName";
                else if (value.length() > 30)
                    value = splitLongCell(value);
                columnNames.add(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    public void initCSO() {
        getConnect();
        deleteTable("CSO-Column-Template");
        String create_sql = "CREATE TABLE \"CSO-Column-Template\" (\"Column Template\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(create_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt = con.createStatement();
            for (String col : CSO_Column) {
                String insert_sql = "INSERT INTO \"CSO-Column-Template\" (\"Column Template\") VALUES (\'" + col + "\')";
                stmt.executeQuery(insert_sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("CSO column template init success.");
        closeAll();
    }

    public void initSCR() {
        getConnect();
        deleteTable("SCR-Column-Template");
        String create_sql = "CREATE TABLE \"SCR-Column-Template\" (\"Column Template\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(create_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt = con.createStatement();
            for (String col : SCR_Column) {
                String insert_sql = "INSERT INTO \"SCR-Column-Template\" (\"Column Template\") VALUES (\'" + col + "\')";
                stmt.executeQuery(insert_sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("SCR column template init success.");
        closeAll();
    }

    public void initPartFinder() {
        getConnect();
        deleteTable("PartFinder-Column-Template");
        String create_sql = "CREATE TABLE \"PartFinder-Column-Template\" (\"Column Template\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(create_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt = con.createStatement();
            for (String col : PartFinder_Column) {
                String insert_sql = "INSERT INTO \"PartFinder-Column-Template\" (\"Column Template\") VALUES (\'" + col + "\')";
                stmt.executeQuery(insert_sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("PartFinder column template init success.");
        closeAll();
    }

    public void initDispatch() {
        getConnect();
        deleteTable("Dispatch-Column-Template");
        String create_sql = "CREATE TABLE \"Dispatch-Column-Template\" (\"Column Template\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(create_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt = con.createStatement();
            for (String col : Dispatch_Column) {
                String insert_sql = "INSERT INTO \"Dispatch-Column-Template\" (\"Column Template\") VALUES (\'" + col + "\')";
                stmt.executeQuery(insert_sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Dispatch column template init success.");
        closeAll();
    }

    public void initComplaints() {
        getConnect();
        deleteTable("Complaints-Column-Template");
        String create_sql = "CREATE TABLE \"Complaints-Column-Template\" (\"Column Template\" VARCHAR(100))";
        try {
            stmt = con.createStatement();
            stmt.executeQuery(create_sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt = con.createStatement();
            for (String col : Complaints_Column) {
                String insert_sql = "INSERT INTO \"Complaints-Column-Template\" (\"Column Template\") VALUES (\'" + col + "\')";
                stmt.executeQuery(insert_sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Complaints column template init success.");
        closeAll();
    }

    /**
     * @param cell format cell to string
     */
    private String getCellValue(Cell cell) {
        String value = null;
        if (cell == null) {
            return null;
        } else {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = DateFormat.getDateInstance().format(cell.getDateCellValue());
                    } else {
                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                        nf.setGroupingUsed(false);
                        value = String.valueOf(nf.format(cell.getNumericCellValue()));
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value = String.valueOf(cell.getNumericCellValue());
                            break;
                        default:
                    }
                    break;
                case Cell.CELL_TYPE_ERROR:
                    value = String.valueOf(cell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                default:
                    break;
            }
            return value;
        }
    }

    /**
     * @param value if cell is longer than 30,
     *              it will be shorten by the first character
     *              after every space.
     */
    private String splitLongCell(String value) {
        String splitValue = "";
        String[] tmpValues = value.split(" ");
        for (String tmpValue : tmpValues) {
            splitValue = splitValue + tmpValue.substring(0, 1) + " ";
        }
        return splitValue.substring(0, splitValue.length() - 1);
    }

    private void getConnect() {
        try {
            Class.forName(GlobalVariables.oracleDriver);
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteTable(String tableName) {
        String delete_sql = String.format("drop table \"%s\"", tableName);
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(delete_sql);
            stmt.close();
        } catch (SQLException ignored) {
        }
    }

    private void closeAll() {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if (con != null)
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void main(String[] args) {
        ExcelsUtils excelsUtils = new ExcelsUtils(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword
        );
//        excelsUtils.initCSO();
//        excelsUtils.initSCR();
//        excelsUtils.initPartFinder();
//        excelsUtils.initDispatch();
//        excelsUtils.initComplaints();
    }
}
