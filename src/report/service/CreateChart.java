package report.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.GlobalVariables;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by myl
 * on 2015/2/8.
 */
public class CreateChart {
    List<List<Integer>> reportList;
    int max = 0;
    String serviceType;

    public CreateChart(List<List<Integer>> reportList, int max, String serviceType) {
        this.reportList = reportList;
        this.max = max;
        this.serviceType = serviceType;
    }

    public void print() throws IOException {
        String path;
        if (serviceType.equals("CSO Report")) {
            path = GlobalVariables.csoReportChartImageUrl;
        } else {
            path = GlobalVariables.scrReportChartImageUrl;
        }
        FileOutputStream fos = new FileOutputStream(path);
        ChartUtilities.writeChartAsJPEG(fos, 1, getJFreeChart(), 1440, 480, null);
        fos.close();
    }

    public JFreeChart getJFreeChart() {
        JFreeChart imgChart = ChartFactory.createLineChart("", "", serviceType,
                getDataSet(), PlotOrientation.VERTICAL, true, true, false);

        imgChart.setBackgroundPaint(Color.white);
        imgChart.setBorderVisible(true);
        TextTitle title = new TextTitle("Trend", new Font("sans-serif", Font.BOLD, 20));
        imgChart.setTitle(title);
        imgChart.getLegend().setItemFont(new Font("sans-serif", Font.PLAIN, 12));
        CategoryPlot categoryplot = (CategoryPlot) imgChart.getPlot();
        categoryplot.setBackgroundPaint(Color.lightGray);
        categoryplot.setRangeGridlinePaint(Color.white);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        setNumberAxis(numberaxis);

        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        setDomainAxis(domainAxis);


        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot
                .getRenderer();
        lineandshaperenderer.setBaseShapesVisible(true);
        lineandshaperenderer
                .setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        lineandshaperenderer.setBaseItemLabelsVisible(true);
        return imgChart;
    }

    private void setDomainAxis(CategoryAxis domainAxis) {
        domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
        domainAxis.setLabelFont(new Font("sans-serif", Font.PLAIN, 18));
        domainAxis.setTickMarksVisible(true);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }

    private void setNumberAxis(NumberAxis numberaxis) {
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setAutoTickUnitSelection(false);
        numberaxis.setLabelFont(new Font("sans-serif", Font.PLAIN, 14));
    }

    private DefaultCategoryDataset getDataSet() {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        if (serviceType.equals("CSO Report")) {
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(3), "# Red CSO",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(4), "# CSO\\'s >60 Days",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(5), "# Red CSO\\'s >60 Days",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(6), "New Open this week",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(8), "New Closed This Week",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
        } else {
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(2), "Total Open SCR",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(3), "# SCR\\'s >60 Days",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(4), "New Open This Week",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
            for (List<Integer> aReportList : reportList) {
                defaultcategorydataset.addValue(aReportList.get(5), "New Closed This Week",
                        aReportList.get(0) + "-FW" + aReportList.get(1));
            }
        }

        return defaultcategorydataset;
    }
}
