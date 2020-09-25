package beleggingspakket.indicatoren;


/*
*   An Indicator object is a line graph with one or two lines.
*   It has as X axis the same axis as the corresponding candle graph
*   Y range has to be determined from the corresponding series
*   The series should have the same number of elements as there are (visible, not dummy) candles in the graph
*/

import beleggingspakket.grafiekenscherm.RSIChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.List;

public class IndicatorClass  {

    private NumberAxis xAxis;
    private NumberAxis yAxis;



    // Maak er een Linechart van
    public LineChart<Number, Number> getLineChart () {
         LineChart<Number, Number> lineChart = null;
        lineChart = new LineChart(xAxis, yAxis);
        lineChart.setCreateSymbols(false);

        if (series2 != null)
            lineChart.getData().add(series2);
        for (XYChart.Series<Number, Number> s : lineChart.getData()) {
            s.getNode().setStyle("-fx-stroke: rgba(13,33,127,0.41); ");
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    // Maak er een RSIChart van. Deze heeft nog een paar extra lijnen.
    public RSIChart getRSIChart(int xAxisWidth) {
        RSIChart rsiChart = new RSIChart(xAxis, yAxis, xAxisWidth);
        rsiChart.setCreateSymbols(false);

        if (series2 != null)
            rsiChart.getData().add(series2);
        for (XYChart.Series<Number, Number> s : rsiChart.getData()) {
            s.getNode().setStyle("-fx-stroke: rgba(13,33,127,0.41); ");
        }
        rsiChart.getData().add(series);
        return (RSIChart) rsiChart;
    }


    private XYChart.Series series = null;
    private XYChart.Series series2 = null;
    private Double yMax = null;
    private Double yMin = null;


    public IndicatorClass(NumberAxis aXAxis,
                          NumberAxis aYAxis,
                          List<Double> aValues,
                          List<Double> aValues2,
                          String indicatorName,
                          int aStartindex,
                          int aEindindex,
                          int aAantalDagenLinks,
                          boolean aToonTickmarks) {

        xAxis = aXAxis;
        yAxis = aYAxis;
        yAxis.setAnimated(false);
        xAxis.setAnimated(false);

        series = new XYChart.Series();
        series.setName(indicatorName);

        if (aValues2 != null) {
            series2 = new XYChart.Series();
            series2.setName(indicatorName + "-signal");
        }

        int iIndex = 0;
        for (int i = aStartindex; i<= aEindindex; i++) {
            double yval = aValues.get(i);
            double yval2 = 0;
            if (yMax == null)
                yMax = yval;
            if (yMin == null)
                yMin = yval;
            if (yval > yMax)
                yMax = yval;
            if (yval < yMin)
                yMin = yval;
            if (aValues2 != null) {
                yval2 = aValues2.get(i);
                if (yval2 > yMax)
                    yMax = yval2;
                if (yval2 < yMin)
                    yMin = yval2;
            }

            series.getData().add(new XYChart.Data((iIndex + aAantalDagenLinks), yval));

            if (series2 != null)
               series2.getData().add(new XYChart.Data((iIndex + aAantalDagenLinks), yval2));
            iIndex++;
        }
        double range = Math.abs(yMax- yMin);
        double extra = range*0.03;
        yMax += extra;
        yMin -= extra;
        if (aToonTickmarks) {
            yAxis = new NumberAxis( yMin, yMax, 1);

        } else {
            yAxis = new NumberAxis( yMin, yMax, range*2);

        }
        yAxis.setAnimated(false);
        yAxis.setLabel(indicatorName);
//        if (!aToonTickmarks)
//            yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                double yvalue = object.doubleValue();
                //return "--------";
                if (yvalue <= yMin + 0.01) {
                    return "--------";
                } else {
                    double result = (double)Math.round(yvalue * 1000d) / 1000d;
                    if (Double.toString(result).length() > 9)
                        return Double.toString(   (double) Math.round(result/1000000)) + " M";
                    else
                        return Double.toString(result);
                }

            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        System.out.println("indicator object created");
    }


}
