package nl.beleggingspakket.indicatoren;


/*
*   An Indicator object is a line graph with one or two lines.
*   It has as X axis the same axis as the corresponding candle graph
*   Y range has to be determined from the corresponding series
*   The series should have the same number of elements as there are (visible, not dummy) candles in the graph
*/

import nl.beleggingspakket.grafiekenscherm.CandlestickClass;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.List;

//public class IndicatorClass extends LineChart<Number, Number> {
public class BarchartIndicator {

    private final CandlestickClass candlestickObject;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;


    public BarChart<Number, Number> getChart() {
        return chart;
    }

    private BarChart chart = null;
    private XYChart.Series series = null;

    private double yMax = -1000;
    private double yMin = 1000;


    public BarchartIndicator(CandlestickClass aCandlestickObject,
                             NumberAxis aXAxis,
                             NumberAxis aYAxis,
                             List<Double> aValues,
                             String indicatorName,
                             int aAantalDagenLinks,
                             int aAantalDagenRechts) {

        //xAxis = aXAxis;
        this.candlestickObject = aCandlestickObject;
        xAxis = new CategoryAxis();



        yAxis = aYAxis;
        yAxis.setAnimated(false);
        xAxis.setAnimated(false);

        series = new XYChart.Series();
        series.setName(indicatorName);

        int iIndex = 1;
        for (int i = 0; i<= aAantalDagenLinks-2; i++) {
            series.getData().add(new XYChart.Data(Integer.toString(iIndex), 0.0));
            iIndex++;
        }

        for (int i = 0; i<= aValues.size()-1; i++) {
            double yval = aValues.get(i);
            if (yval > yMax)
                yMax = yval;
            if (yval < yMin)
                yMin = yval;

            series.getData().add(new XYChart.Data(Integer.toString(iIndex), yval));
            iIndex++;
        }
        for (int i = 0; i<= aAantalDagenRechts-2; i++) {
            series.getData().add(new XYChart.Data(Integer.toString(iIndex), 0.0));
            iIndex++;
        }
        double range = Math.abs(yMax- yMin);
        double extra = range*0.03;
        yMax += extra;
        yMin -= extra;
        yAxis = new NumberAxis( yMin, yMax, range*2);
        yAxis.setTickMarkVisible(false);
        yAxis.setAnimated(false);
        yAxis.setLabel(indicatorName);
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                double yvalue = object.doubleValue();
                return "--------";
//                if (yvalue <= yMin + 0.01) {
//                    return "--------";
//                } else {
//                    double result = (double)Math.round(yvalue * 1000d) / 1000d;
//                    return Double.toString(result);
//                }

            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });

        chart = new BarChart(xAxis, yAxis);


        chart.getData().add(series);

        System.out.println("barchart indicator object created");
    }


}
