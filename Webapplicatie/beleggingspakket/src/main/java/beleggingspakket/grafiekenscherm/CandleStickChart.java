/*
 * Copyright (c) 2008, 2016, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package beleggingspakket.grafiekenscherm;

import beleggingspakket.GrafiekenschermController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A candlestick chart is a style of bar-chart used primarily to describe
 * price movements of a security, derivative, or currency over time.
 *
 * The Data Y value is used for the opening price and then the
 * close, high and low values are stored in the Data's
 * extra value property using a CandleStickExtraValues object.
 *
 *
 * 17 feb 2020 Candlewidth wordt vastgesteld aan de hand van de beschikare ruimte tussen de handelsdagen
 *
 */
public class CandleStickChart extends LineChart<Number, Number> {
    private GrafiekenschermController myGrafiekenschermController;
    private CandlestickClass myCandlestickObject;

    public double getChartHeight() {
        return chartHeight;
    }

    private double chartHeight = 0;

    /**
     * Construct a new CandleStickChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public CandleStickChart(Axis<Number> xAxis, Axis<Number> yAxis, CandlestickClass aCandlestickobject) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.myCandlestickObject = aCandlestickobject;

        //System.out.println(System.getProperty("java.class.path"));
        Class<?> myClass = getClass();
        URL url = myClass.getResource("CandleStickChart.css");
        String candleStickChartCss = url.toExternalForm();

        //final String candleStickChartCss =
        //    getClass().getResource("CandleStickChart.css").toExternalForm();
        getStylesheets().add(candleStickChartCss);
        setAnimated(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        this.setMousehandler();
    }

    public GrafiekenschermController getMyGrafiekenschermController() {
        return myGrafiekenschermController;
    }

    public void setMyGrafiekenschermController(GrafiekenschermController myGrafiekenschermControllerBackup) {
        this.myGrafiekenschermController = myGrafiekenschermController;
    }




    private Axis<Number> xAxis;
    private Axis<Number> yAxis;


//    public Lines getSlopedLines() {
//        return slopedLines;
//    }


//    public Lines getMyLines() {
//        return myLines;
//    }


    public void setMousehandler() {
        this.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {

                onMousePressedButtonDown(mouseEvent);

            }
        });
    }

    private void onMousePressedButtonDown(MouseEvent mouseEvent) {
        Point2D mouseSceneCoords = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        double x =   xAxis.sceneToLocal(mouseSceneCoords).getX();
        double y = yAxis.sceneToLocal(mouseSceneCoords).getY();

        Number x1 =  xAxis.getValueForDisplay(x);
        Number y1 =  yAxis.getValueForDisplay(y);

        // Check of er objecten zijn geraakt
        // dit kan zijn:
        // een lijn
        // een candle
        // niets


        Line line = inBuurtVanLijn((double) x1, (double) y1);
        Line line2 = inBuurtVanSlopedLijn((double) x1, (double) y1);
        if (line != null) {
            myGrafiekenschermController.lineClicked((double) x1, (double) y1, line, true);
        } else if (line2 != null) {
            myGrafiekenschermController.lineClicked((double) x1, (double) y1, line2, false);
        } else {
            myGrafiekenschermController.mouseClick(x1, y1);
        }
    }

    private boolean isVertDichtbij(double aY1, double aY2) {
        if ( Math.abs(aY1 - aY2) < 0.5)
            return true;
        return false;
    }

    private boolean isInBuurtVanCandle(double x1, double y1) {

        return false;
    }


    private Line inBuurtVanLijn(double x1, double y1) {
        for (Line line: myCandlestickObject.mapHorizLines()) {
            if (isVertDichtbij(line.getP1().getY(), y1))
                return line;
        }
        return null;
    }

    private Line inBuurtVanSlopedLijn(double aX, double aY) {
        for (Line line: myCandlestickObject.mapSlopedLines()) {
            // bereken de y coord bij x1 op de lijn
            double x1 = (line.getP1().getX());
            double y1 = (line.getP1().getY());
            double x2 = (line.getP2().getX());
            double y2 = (line.getP2().getY());
            // bereken nu de y horende bij x1 op de lijn
            double y = y1 + (y2 - y1) / (x2 - x1) * (aX - x1);
            if (isVertDichtbij(aY, y))
                return line;
        }
        return null;
    }

    /**   (NOG) NIET GEBRUIKT
     * Construct a new CandleStickChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The actual data list to use so changes will be
     *             reflected in the chart.
     */
//    public CandleStickChart(Axis<Number> xAxis, Axis<Number> yAxis,
//                            ObservableList<Series<Number, Number>> data) {
//        this(xAxis, yAxis);
//        this.xAxis = xAxis;
//        this.yAxis = yAxis;
//        setData(data);
//        this.setMousehandler();
//    }

    /** Called to update and layout the content for the plot */
    @Override
    public void layoutPlotChildren() {
        boolean firstTime = true;
        // we have nothing to layout if no data is present
        //getChartChildren().add(new Line(10,01, 50,50));
        if (getData() == null) {
            return;
        }
        // update candle positions
        for (int index = 0; index < getData().size(); index++) {

            Series<Number, Number> series = getData().get(index);
            Iterator<Data<Number, Number>> iter =
                getDisplayedDataIterator(series);
            Path seriesPath = null;
            if (series.getNode() instanceof Path) {
                seriesPath = (Path) series.getNode();
                seriesPath.getElements().clear();
            }

            while (iter.hasNext()) {
                Axis<Number> yAxis = getYAxis();
                Data<Number, Number> item = iter.next();
                Number X = getCurrentDisplayedXValue(item);
                Number Y = getCurrentDisplayedYValue(item);
                double x = getXAxis().getDisplayPosition(X);
                double y = getYAxis().getDisplayPosition(Y);
                Node itemNode = item.getNode();
                CandleStickExtraValues extra =
                    (CandleStickExtraValues)item.getExtraValue();
                if (itemNode instanceof Candle && extra != null) {
                    double close = yAxis.getDisplayPosition(extra.getClose());
                    double high = yAxis.getDisplayPosition(extra.getHigh());
                    double low = yAxis.getDisplayPosition(extra.getLow());
                    // calculate candle width
                    double candleWidth = -1;
                    if (getXAxis() instanceof NumberAxis) {
                         // use 90% width between ticks
                        NumberAxis xa = (NumberAxis) getXAxis();
                        double unit = xa.getDisplayPosition(xa.getTickUnit());
                        candleWidth = unit * 0.2;
                    }
                    // update candle
                    Candle candle = (Candle)itemNode;
                    candle.update(close - y, high - y, low - y, candleWidth);
                    candle.updateTooltip(item.getYValue().doubleValue(),
                                         extra.getClose(), extra.getHigh(),
                                         extra.getLow(),
                            extra.getDateString()
                            );

                    // position the candle
                    candle.setLayoutX(x);
                    candle.setLayoutY(y);
                }

                // Teken alle lijnen
                if (firstTime == true) {
                        firstTime = false;

                        tekenLijnen(seriesPath);
                }
            }
        }
        chartHeight = this.getHeight();
    }

    private Path myPath;
    private void tekenLijn(Line aLine) {
        double x1 = getXAxis().getDisplayPosition(aLine.getP1().getX());
        double y1 = getYAxis().getDisplayPosition(aLine.getP1().getY());
        double x2 = getXAxis().getDisplayPosition(aLine.getP2().getX());
        double y2 = getYAxis().getDisplayPosition(aLine.getP2().getY());

        MoveTo moveto = new MoveTo(x1,y1);
        myPath.getElements().add(moveto);
        LineTo lineto = new LineTo(x2, y2);
        myPath.getElements().add(lineto);
    }

    // Teken alle lijnen die aanwezig zijn in het array
    private void tekenLijnen(Path seriesPath) {
        myPath = seriesPath;
        for (Line line: myCandlestickObject.mapHorizLines()) {
           tekenLijn(line);
        }
        for (Line line: myCandlestickObject.mapSlopedLines()) {
            System.out.println("sloped line found");
            tekenLijn(line);
        }
    }

    @Override protected void dataItemChanged(Data<Number, Number> item) {
    }

    @Override protected void dataItemAdded(Series<Number, Number> series,
                                           int itemIndex,
                                           Data<Number, Number> item) {
        Node candle = createCandle(getData().indexOf(series), item, itemIndex);
        if (shouldAnimate()) {
            candle.setOpacity(0);
            getPlotChildren().add(candle);
            // fade in new candle
            final FadeTransition ft =
                new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(candle);
        }
        // always draw average line on top
        if (series.getNode() != null) {
            series.getNode().toFront();
        }
    }

    @Override protected void dataItemRemoved(Data<Number, Number> item,
                                             Series<Number, Number> series) {
        final Node candle = item.getNode();
        if (shouldAnimate()) {
            // fade out old candle
            final FadeTransition ft =
                new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(0);
            ft.setOnFinished((ActionEvent actionEvent) -> {
                getPlotChildren().remove(candle);
            });
            ft.play();
        } else {
            getPlotChildren().remove(candle);
        }
    }

    @Override protected void seriesAdded(Series<Number, Number> series,
                                         int seriesIndex) {
        // handle any data already in series
        for (int j = 0; j < series.getData().size(); j++) {
            Data item = series.getData().get(j);
            Node candle = createCandle(seriesIndex, item, j);
            if (shouldAnimate()) {
                candle.setOpacity(0);
                getPlotChildren().add(candle);
                // fade in new candle
                final FadeTransition ft =
                    new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(1);
                ft.play();
            } else {
                getPlotChildren().add(candle);
            }
        }
        // create series path
        Path seriesPath = new Path();
        seriesPath.getStyleClass().setAll("candlestick-average-line",
                                          "series" + seriesIndex);
        series.setNode(seriesPath);
        getPlotChildren().add(seriesPath);
    }

    @Override protected void seriesRemoved(Series<Number, Number> series) {
        // remove all candle nodes
        for (Data<Number, Number> d : series.getData()) {
            final Node candle = d.getNode();
            if (shouldAnimate()) {
                // fade out old candle
                final FadeTransition ft =
                    new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(0);
                ft.setOnFinished((ActionEvent actionEvent) -> {
                    getPlotChildren().remove(candle);
                });
                ft.play();
            } else {
                getPlotChildren().remove(candle);
            }
        }
    }

    /**
     * Create a new Candle node to represent a single data item
     *
     * @param seriesIndex The index of the series the data item is in
     * @param item        The data item to create node for
     * @param itemIndex   The index of the data item in the series
     * @return New candle node to represent the give data item
     */
    private Node createCandle(int seriesIndex, final Data item,
                              int itemIndex) {
        Node candle = item.getNode();
        // check if candle has already been created
        if (candle instanceof Candle) {
            ((Candle)candle).setSeriesAndDataStyleClasses("series" + seriesIndex,
                                                          "data" + itemIndex);
        } else {
            candle = new Candle("series" + seriesIndex, "data" + itemIndex);
            item.setNode(candle);
        }
        return candle;
    }

    /**
     * This is called when the range has been invalidated and we need to
     * update it. If the axis are auto ranging then we compile a list of
     * all data that the given axis has to plot and call invalidateRange()
     * on the axis passing it that data.
     */
    @Override
    protected void updateAxisRange() {
        // For candle stick chart we need to override this method as we need
        // to let the axis know that they need to be able to cover the area
        // occupied by the high to low range not just its center data value.
        final Axis<Number> xa = getXAxis();
        final Axis<Number> ya = getYAxis();
        List<Number> xData = null;
        List<Number> yData = null;
        if (xa.isAutoRanging()) {
            xData = new ArrayList<Number>();
        }
        if (ya.isAutoRanging()) {
            yData = new ArrayList<Number>();
        }
        if (xData != null || yData != null) {
            for (Series<Number, Number> series : getData()) {
                for (Data<Number, Number> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                    }
                    if (yData != null) {
                        CandleStickExtraValues extras =
                            (CandleStickExtraValues)data.getExtraValue();
                        if (extras != null) {
                            yData.add(extras.getHigh());
                            yData.add(extras.getLow());
                        } else {
                            yData.add(data.getYValue());
                        }
                    }
                }
            }
            if (xData != null) {
                xa.invalidateRange(xData);
            }
            if (yData != null) {
                ya.invalidateRange(yData);
            }
        }
    }


//    public void delHorLine(Line aLine) {
//                myLines.delLine(aLine);
//    }
}
