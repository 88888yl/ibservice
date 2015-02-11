package complaints.servlet;

import complaints.database.ImportDBfromAllSheets;
import utils.GlobalVariables;

/**
 * Created by myl
 * on 2015/2/11.
 */
public class test {
    public static void main(String[] args) {
        ImportDBfromAllSheets importDBfromAllSheets = new ImportDBfromAllSheets(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        importDBfromAllSheets.insertTables();
    }
}
