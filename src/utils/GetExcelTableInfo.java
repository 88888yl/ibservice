package utils;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by myl
 * on 2015/1/22.
 */
public class GetExcelTableInfo {
    private String path;

    public GetExcelTableInfo(String path) {
        this.path = path;
    }

    public String[] getExcelTableNames() {
        return new ExcelsUtils().getExcelsName(path);
    }

    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<String>();
        String[] excelTableNames = getExcelTableNames();
        for (String excelTableName : excelTableNames) {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(path + excelTableName);
                Workbook workbook = WorkbookFactory.create(inputStream);
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    if (!tableNames.contains(workbook.getSheetName(i)))
                        tableNames.add(path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('\\') + 5)
                                + '-' + workbook.getSheetName(i));
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return tableNames;
    }

    public List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<String>();
        String[] excelTableNames = getExcelTableNames();
        for (String excelTableName : excelTableNames) {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(path + excelTableName);
                Workbook workbook = WorkbookFactory.create(inputStream);
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    if (tableName.substring(tableName.indexOf('-') + 1).equals(workbook.getSheetName(i))) {
                        Sheet sheet = workbook.getSheet(tableName.substring(tableName.indexOf('-') + 1));
                        if (sheet == null) {
                            return Collections.emptyList();
                        }
                        for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                            Cell cell = sheet.getRow(0).getCell(j);
                            if (cell == null) {
                                columnNames.add("empty-" + j);
                            } else {
                                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    columnNames.add(String.valueOf(cell.getNumericCellValue()));
                                } else {
                                    columnNames.add(String.valueOf(cell.getStringCellValue()));
                                }
                            }
                        }
                    }
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return columnNames;
    }

    public List<List<String>> getRows(String tableName) {
        String[] excelTableNames = getExcelTableNames();
        List<List<String>> rows = new ArrayList<List<String>>();
        for (String excelTableName : excelTableNames) {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(path + excelTableName);
                Workbook workbook = WorkbookFactory.create(inputStream);
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    if (tableName.substring(tableName.indexOf('-') + 1).equals(workbook.getSheetName(i))) {
                        Sheet sheet = workbook.getSheet(tableName.substring(tableName.indexOf('-') + 1));
                        if (sheet == null) {
                            return Collections.emptyList();
                        }
                        for (int j = 0; j < sheet.getLastRowNum(); j++) {
                            List<String> rowList = new ArrayList<String>();
                            for (int n = 0; n < sheet.getRow(0).getLastCellNum(); n++) {
                                Cell cell = sheet.getRow(j).getCell(n);
                                String value = "";
                                if (cell == null) {
                                    rowList.add("");
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
                                    rowList.add(value);
                                }
                            }
                            rows.add(rowList);
                        }
                    }
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rows;
    }
}
