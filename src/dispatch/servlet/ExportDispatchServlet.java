package dispatch.servlet;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import utils.ExportToExcel;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/2.
 */
public class ExportDispatchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String store = request.getParameter("store");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=export-dispatch-" + dateStr + ".xls");
        ServletOutputStream os = response.getOutputStream();

        ExportToExcel exportToExcel = new ExportToExcel(
                GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
        exportToExcel.setStore(store);
        List<List<String>> rows = exportToExcel.getRowsFromStr("Dispatch-All");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Search Result");
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < rows.get(i).size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rows.get(i).get(j));
                if (i == 0) {
                    cell.setCellStyle(style);
                }
            }
        }
        try {
            os.flush();
            workbook.write(os);
            os.close();
            System.out.println("Excel export success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
