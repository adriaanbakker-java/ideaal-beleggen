package beleggingspakket.grafiekenscherm;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


public class RSIChart extends LineChart<Number, Number> {


    private final int myXaxisWidth;

    /*
         *  XAxisWidth is the nr of trading days plus dummy candle postion nr of extra space left and right
         *  i.e. 0 + aantalLinks is the leftmost candle x position, aantalRechts + aantalLinks + aantal candles
         *  is the rightmost position on the X axis
         */
        public RSIChart(Axis<Number> xAxis, Axis<Number> yAxis,
                        int xAxisWidth) {
            super(xAxis, yAxis);
            myXaxisWidth = xAxisWidth;
        }

        // Do the layoutPlotChildren to paint the RSI line on the graph screen.
        // In addition, add horizontal line at position 30 and 70 percent.
        @Override
        public void layoutPlotChildren() {
            super.layoutPlotChildren();
            //System.out.println("LAYOUTPLOTCHILDREN FROM RSICHART");
            if (getData() == null) {
                return;
            }
            // Find the so called Path, I have no idea what that even is, probably connected to a data series
            // You will need the path to add lines and such
            Path seriesPath = null;
            // Create a second path if there is only 1, there should be only one initially
            // In case it already exists, retrieve the path
            // Set this path to the rgb values of blueish
            String rgba_horline = "-fx-stroke: rgba(13,33,60,0.41); ";
            Series series2 = null;
            if (getData().size() == 1) {
                series2 = new Series<Number, Number>();
                getData().add(series2);
                seriesPath = (Path) series2.getNode();
                seriesPath.setStyle(rgba_horline);
            } else
                if (getData().size() == 2) {
                    Series<Number, Number> series = getData().get(1);
                    if (series.getNode() instanceof Path) {
                        seriesPath = (Path) series.getNode();
                        seriesPath.setStyle(rgba_horline);
                    }
                }

            // add the horizontal lines to this path in the graph
            if (seriesPath != null) {
                double ydisp30 = getYAxis().getDisplayPosition(30);
                double ydisp70 = getYAxis().getDisplayPosition(70);
                double xmax = getXAxis().getDisplayPosition( myXaxisWidth);
                MoveTo moveto = new MoveTo(0, ydisp30);

                seriesPath.getElements().add(moveto);
                LineTo lineto = new LineTo(xmax, ydisp30);
                seriesPath.getElements().add(lineto);

                MoveTo moveto2 = new MoveTo(0, ydisp70);
                seriesPath.getElements().add(moveto2);
                LineTo lineto2 = new LineTo(xmax, ydisp70);
                seriesPath.getElements().add(lineto2);
            }

        } // overridden layoutPlotChildren
    }
