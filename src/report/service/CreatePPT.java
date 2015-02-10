package report.service;


import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import utils.GlobalVariables;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/10.
 */
public class CreatePPT {
    public CreatePPT() {
    }

    public void initCSO(List<List<Integer>> tableList) {
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();

        XSLFTable tbl = slide.createTable();
        double width = ppt.getPageSize().getWidth();
        double height = ppt.getPageSize().getHeight();
        tbl.setAnchor(new Rectangle2D.Double(30, 15, width - 60, height / 2 - 15));
        int numColumns = tableList.size() + 2;
        if (tableList.size() >= 8) {
            numColumns = 10;
        }
        int numRows = 12;
        XSLFTableRow headerRow = tbl.addRow();
        headerRow.setHeight(25);

        XSLFTableCell th0 = headerRow.addCell();
        XSLFTextParagraph p0 = th0.addNewTextParagraph();
        p0.setTextAlign(TextAlign.CENTER);
        XSLFTextRun r0 = p0.addNewTextRun();
        r0.setText("RRF QTO Metrics");
        r0.setBold(true);
        r0.setFontSize(14);
        r0.setFontColor(Color.white);
        th0.setFillColor(new Color(79, 129, 189));
        th0.setBorderBottom(2);
        th0.setBorderBottomColor(Color.white);
        tbl.setColumnWidth(0, 130);

        XSLFTableCell th1 = headerRow.addCell();
        XSLFTextParagraph p1 = th1.addNewTextParagraph();
        p1.setTextAlign(TextAlign.CENTER);
        XSLFTextRun r1 = p1.addNewTextRun();
        r1.setText("GOAL");
        r1.setBold(true);
        r1.setFontSize(14);
        r1.setFontColor(Color.white);
        th1.setFillColor(new Color(79, 129, 189));
        th1.setBorderBottom(2);
        th1.setBorderBottomColor(Color.white);
        tbl.setColumnWidth(1, 50);

        for(int i = 2; i < numColumns; i++) {
            XSLFTableCell th = headerRow.addCell();
            XSLFTextParagraph p = th.addNewTextParagraph();
            p.setTextAlign(TextAlign.RIGHT);
            XSLFTextRun r = p.addNewTextRun();
            r.setText("FW " + tableList.get(tableList.size() - numColumns + i).get(1).toString());
            r.setBold(true);
            r.setFontSize(14);
            r.setFontColor(Color.white);
            th.setFillColor(new Color(79, 129, 189));
            th.setBorderBottom(2);
            th.setBorderBottomColor(Color.white);

            tbl.setColumnWidth(i, 60);
        }

        String[] titleStr = {"# All Open CSO", "# Red CSO", "# CSO's >60 Days",
                "# Red CSO's >60 Days", "New Open this week", "Red Open This Week",
                "New Closed This Week", "Red Closed This Week", "P95 All Open CSO",
                "P95 Red CSO", "P95 Non Red CSO", "P50 Red CSO"};
        for(int rownum = 0; rownum < numRows; rownum ++){

            XSLFTableRow tr = tbl.addRow();
            tr.setHeight(15);
            XSLFTableCell cell0 = tr.addCell();
            XSLFTableCell cell1 = tr.addCell();
            XSLFTextParagraph p00 = cell0.addNewTextParagraph();
            XSLFTextParagraph p11 = cell1.addNewTextParagraph();
            p00.setTextAlign(TextAlign.RIGHT);
            p11.setTextAlign(TextAlign.RIGHT);
            XSLFTextRun r00 = p00.addNewTextRun();
            XSLFTextRun r11 = p11.addNewTextRun();
            r00.setText(titleStr[rownum]);
            r00.setFontSize(12);
            r11.setText("0");
            r11.setFontSize(12);
            if(rownum % 2 == 0) {
                cell0.setFillColor(new Color(208, 216, 232));
                cell1.setFillColor(new Color(208, 216, 232));
            }
            else {
                cell0.setFillColor(new Color(239, 253, 250));
                cell1.setFillColor(new Color(239, 253, 250));
            }

            for(int i = 2; i < numColumns; i++) {
                XSLFTableCell cell = tr.addCell();
                XSLFTextParagraph p = cell.addNewTextParagraph();
                p.setTextAlign(TextAlign.RIGHT);
                XSLFTextRun r = p.addNewTextRun();
                r.setText(tableList.get(tableList.size() - numColumns + i).get(rownum + 2).toString());
                r.setFontSize(12);
                if(rownum % 2 == 0)
                    cell.setFillColor(new Color(208, 216, 232));
                else
                    cell.setFillColor(new Color(239, 253, 250));
            }

        }

        try {
            byte[] pictureData = IOUtils.toByteArray(new FileInputStream(GlobalVariables.csoReportChartImageUrl));
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(pictureData));
            double imageWidth = bufferedImage.getWidth();
            double imageHeight = bufferedImage.getHeight();
            if (imageWidth > width - 60) {
                imageWidth = width - 60;
            }
            if (imageHeight > height / 2 - 30) {
                imageHeight = height / 2 - 30;
            }
            int idx = ppt.addPicture(pictureData, XSLFPictureData.PICTURE_TYPE_JPEG);
            XSLFPictureShape pic = slide.createPicture(idx);
            pic.setAnchor(new Rectangle2D.Double(30, 300, imageWidth, imageHeight));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream out = new FileOutputStream(GlobalVariables.csoReportPPT);
            ppt.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initSCR(List<List<Integer>> tableList) {
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();

        XSLFTable tbl = slide.createTable();
        double width = ppt.getPageSize().getWidth();
        double height = ppt.getPageSize().getHeight();
        tbl.setAnchor(new Rectangle2D.Double(30, 15, width - 60, height / 2 - 15));
        int numColumns = tableList.size() + 2;
        if (tableList.size() >= 8) {
            numColumns = 10;
        }
        int numRows = 6;
        XSLFTableRow headerRow = tbl.addRow();
        headerRow.setHeight(25);

        XSLFTableCell th0 = headerRow.addCell();
        XSLFTextParagraph p0 = th0.addNewTextParagraph();
        p0.setTextAlign(TextAlign.CENTER);
        XSLFTextRun r0 = p0.addNewTextRun();
        r0.setText("RRF QTO Metrics");
        r0.setBold(true);
        r0.setFontSize(14);
        r0.setFontColor(Color.white);
        th0.setFillColor(new Color(79, 129, 189));
        th0.setBorderBottom(2);
        th0.setBorderBottomColor(Color.white);
        tbl.setColumnWidth(0, 130);

        XSLFTableCell th1 = headerRow.addCell();
        XSLFTextParagraph p1 = th1.addNewTextParagraph();
        p1.setTextAlign(TextAlign.CENTER);
        XSLFTextRun r1 = p1.addNewTextRun();
        r1.setText("GOAL");
        r1.setBold(true);
        r1.setFontSize(14);
        r1.setFontColor(Color.white);
        th1.setFillColor(new Color(79, 129, 189));
        th1.setBorderBottom(2);
        th1.setBorderBottomColor(Color.white);
        tbl.setColumnWidth(1, 50);

        for(int i = 2; i < numColumns; i++) {
            XSLFTableCell th = headerRow.addCell();
            XSLFTextParagraph p = th.addNewTextParagraph();
            p.setTextAlign(TextAlign.RIGHT);
            XSLFTextRun r = p.addNewTextRun();
            r.setText("FW " + tableList.get(tableList.size() - numColumns + i).get(1).toString());
            r.setBold(true);
            r.setFontSize(14);
            r.setFontColor(Color.white);
            th.setFillColor(new Color(79, 129, 189));
            th.setBorderBottom(2);
            th.setBorderBottomColor(Color.white);

            tbl.setColumnWidth(i, 60);
        }

        String[] titleStr = {"# All Open SCR", "# SCR's >60 Days", "New Open This Week",
                "New Closed This Week", "P95 All Open SCR", "P50 SCR"};
        for(int rownum = 0; rownum < numRows; rownum ++){

            XSLFTableRow tr = tbl.addRow();
            tr.setHeight(15);
            XSLFTableCell cell0 = tr.addCell();
            XSLFTableCell cell1 = tr.addCell();
            XSLFTextParagraph p00 = cell0.addNewTextParagraph();
            XSLFTextParagraph p11 = cell1.addNewTextParagraph();
            p00.setTextAlign(TextAlign.RIGHT);
            p11.setTextAlign(TextAlign.RIGHT);
            XSLFTextRun r00 = p00.addNewTextRun();
            XSLFTextRun r11 = p11.addNewTextRun();
            r00.setText(titleStr[rownum]);
            r00.setFontSize(12);
            r11.setText("0");
            r11.setFontSize(12);
            if(rownum % 2 == 0) {
                cell0.setFillColor(new Color(208, 216, 232));
                cell1.setFillColor(new Color(208, 216, 232));
            }
            else {
                cell0.setFillColor(new Color(239, 253, 250));
                cell1.setFillColor(new Color(239, 253, 250));
            }

            for(int i = 2; i < numColumns; i++) {
                XSLFTableCell cell = tr.addCell();
                XSLFTextParagraph p = cell.addNewTextParagraph();
                p.setTextAlign(TextAlign.RIGHT);
                XSLFTextRun r = p.addNewTextRun();
                r.setText(tableList.get(tableList.size() - numColumns + i).get(rownum + 2).toString());
                r.setFontSize(12);
                if(rownum % 2 == 0)
                    cell.setFillColor(new Color(208, 216, 232));
                else
                    cell.setFillColor(new Color(239, 253, 250));
            }

        }
        try {
            byte[] pictureData = IOUtils.toByteArray(new FileInputStream(GlobalVariables.scrReportChartImageUrl));
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(pictureData));
            double imageWidth = bufferedImage.getWidth();
            double imageHeight = bufferedImage.getHeight();
            if (imageWidth > width - 60) {
                imageWidth = width - 60;
            }
            if (imageHeight > height / 2 - 30) {
                imageHeight = height / 2 - 30;
            }
            int idx = ppt.addPicture(pictureData, XSLFPictureData.PICTURE_TYPE_JPEG);
            XSLFPictureShape pic = slide.createPicture(idx);
            pic.setAnchor(new Rectangle2D.Double(30, 150, imageWidth, imageHeight));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream out = new FileOutputStream(GlobalVariables.scrReportPPT);
            ppt.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
