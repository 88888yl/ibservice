package partfinder.import_db;

import partfinder.database.ExcelToDB;
import partfinder.database.InfoToDB;
import utils.ExcelLoader;
import partfinder.utils.FindFather;
import utils.GlobalVariables;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by myl on 14-10-9.
 */
import java.io.FilenameFilter;
import java.util.Map;

public class ImportBom {

    public static void main(String[] args) {
        ImportBom importBom = new ImportBom();
        importBom.main_core();
    }

    public void main_core() {

        String[] files = GetFileName();
        for (int i = 0; i < files.length; i++) {
//            if (!files[i].contains("(Up to date)")) {
                String table_name = GetTableName(files[i]);
                String table_desc = GetTableDesc(files[i]);
                Map<String, String> table_info = GetTableInfo(files[i]);

                /** create table from excels data */
                ExcelToDB excelToDB = new ExcelToDB(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword,
                        GlobalVariables.bomPath + files[i], table_name, table_desc);
                excelToDB.getConnect();
                excelToDB.deleteTableFromExcel();
                excelToDB.createTableFromExcel();
                excelToDB.excelToTable();
                excelToDB.closeAll();

                /** create table from excels information */
                InfoToDB infoToDB = new InfoToDB(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword,
                        GlobalVariables.bomPath + files[i], table_name.replaceAll("BOM", "I"));
                infoToDB.setTableInfo(table_info);
                infoToDB.getConnect();
                infoToDB.deleteTableFromInfo();
                infoToDB.createTableFromInfo();
                infoToDB.infoToTable();
                infoToDB.closeAll();

                /** insert fathers in excel data tables */
                FindFather findFather = new FindFather(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword,
                        table_name);
                findFather.getConnect();
                findFather.insertFatherNumber();
                findFather.closeAll();

//                String excel = GlobalVariables.bomPath + files[i];
//                File excelFile = new File(excel);
//                excelFile.renameTo(new File(GlobalVariables.bomPath + "(Up to date)" + files[i]));
//            }
        }
    }

    public String[] GetFileName() {
        File file = new File(GlobalVariables.bomPath);
        String[] files = file.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".xlsx") || name.endsWith(".xls") || name.endsWith(".slk"));
            }
        });
        System.out.println("Find " + files.length + " excel files in " + GlobalVariables.bomPath);
        return files;
    }

    public String GetTableName(String fileName) {
        ExcelLoader loader = new ExcelLoader(GlobalVariables.bomPath + fileName);
        List<List<String>> rows = loader.loadData(0);
        String table_number = rows.get(1).get(1);
        table_number = table_number.substring(0, 7);
        String table_version = rows.get(1).get(2);
        return "BOM_" + table_number + "_" + table_version;
    }

    public String GetTableDesc(String fileName) {
        ExcelLoader loader = new ExcelLoader(GlobalVariables.bomPath + fileName);
        List<List<String>> rows = loader.loadData(0);
        return rows.get(2).get(1);
    }

    public Map<String, String> GetTableInfo(String fileName) {
        String[] rowName = {
                "Report_Name",
                "Part_rev",
                "Description",
                "Nbr_of_level",
                "Excel_Date",
                "State"};
        ExcelLoader loader = new ExcelLoader(GlobalVariables.bomPath + fileName);
        List<List<String>> rows = loader.loadData(0);

        Map<String, String> infoList = new HashMap<String, String>();
        infoList.put(rowName[0], rows.get(0).get(1));
        infoList.put(rowName[1], rows.get(1).get(1));
        infoList.put(rowName[2], rows.get(2).get(1));
        infoList.put(rowName[3], rows.get(3).get(1));
        infoList.put(rowName[4], rows.get(4).get(1));
        infoList.put(rowName[5], rows.get(5).get(1));

        return infoList;
    }
}