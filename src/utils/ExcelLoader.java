package utils;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcelLoader {

    private Workbook workbook = null;

    public ExcelLoader(String excel) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(excel);
            workbook = WorkbookFactory.create(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Init Excel Loader Error", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Close InputStream Error", e);
                }
            }
        }
    }

    public List<List<String>> loadData(int sheetNumber) {
        Sheet sheet = workbook.getSheetAt(sheetNumber);

        if (sheet == null) {
            return Collections.emptyList();
        }

        List<List<String>> ret = new ArrayList<List<String>>(sheet.getTopRow());


        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            List<String> rowlist = new ArrayList<String>();
            for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                rowlist.add(sheet.getRow(i).getCell(j) == null ? "" : sheet.getRow(i).getCell(j).toString());
            }
            ret.add(rowlist);
        }

//        for (Row row : sheet) {
//            List<String> rowlist = new ArrayList<String>();
//            for (Cell cell : row) {
//                rowlist.add(cell.toString());
//            }
//            ret.add(rowlist);
//        }
        return ret;
    }
}
