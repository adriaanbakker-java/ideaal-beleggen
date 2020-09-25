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

import beleggingspakket.Koersen.DayPriceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;


import java.util.ArrayList;

//import org.openjfx.log;

/*
*  De CandleStickClass is verantwoordelijk voor het initialiseren van de parameters
* die de mapping bepalen tussen de koersreeks en de grafiek (dwz het bijbehorende deel van de koersreeks mappen op de grafiek),
*  het vertalen van coordinaten naar de bijbehorende candles (vertalen van index tussen grafiek en koersreeks),
* het plaatsen van de candles en het afhandelen van de events zoomen en pannen in de grafiek.

*    createCandlechart() in the GrafiekenschermController calls createContent()
     to create the candlestickgraph screen.
     CreateContent() creates a new chart object
     This chart object has both a reference to the candlestick object and the controller.
     Because the chart object is destroyed when zooming, panning etcetera,
     lines have to be hosted by this candlestickClass object
     The lines objects are queried by the chart object in order to be drawn on the canvas.
     In order to draw lines correctly after zooming and panning, the original dates for the
     x coordinates have to be stored within the line objects and a mapping has to be performed

* */

public class CandlestickClass {
    private Lines horLines = new Lines();
    private int lineSeqNr = 0;  // voorzie lijnen van een seqnr om ze terug te kunnen vinden na mapping

    private Lines slopedLines = new Lines();

    public CandlestickClass(ArrayList<DayPriceRecord> dayPriceArray,
                            String gekozenAandeel,
                            int aAantalBeursdagenKoersreeks,
                            int aAantalDagenRetro) throws Exception {

        myDayPriceArray = dayPriceArray;
        init(gekozenAandeel,
                aAantalBeursdagenKoersreeks,
                aAantalDagenRetro);
    }


    public CandleStickChart createContent(String gekozenAandeel) throws Exception {
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName(gekozenAandeel);
        vulGrafiekMetCandles(series);


        xAxis = new NumberAxis(0, aantalBeursdagenGrafiek - 1, 5);
        xAxis.setMinorTickCount(0);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int indexGrafiek = object.intValue();
                Integer indexReeks = txNaarKoersindex(indexGrafiek);
                String datum = "";
                if (indexReeks != null) {
                    DayPriceRecord dpr = myDayPriceArray.get(startindex +  indexReeks);
                    datum = dpr.printDate();
                }
                return datum;
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });

        double bereikV = maxPrice - minPrice;
        double extraV = bereikV * 0.1;

        double lowerBound = minPrice - extraV;
        double upperBound = maxPrice + extraV;

        yAxis = new NumberAxis( lowerBound, upperBound, 1);
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                double yvalue = object.doubleValue();
                if (yvalue <= lowerBound) {
                    return "--------";
                } else {
                    double result = (double)Math.round(yvalue * 1000d) / 1000d;
                    return Double.toString(result);
                }

            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });


        myChart = new CandleStickChart(xAxis, yAxis, this);
        // setup chart
        //xAxis.setLabel("Datum");
        yAxis.setLabel("Prijs");
        //yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        //yAxis.setScaleY(2.0);
        // add starting data


        // Voeg de series toe aan het chart object
        ObservableList<XYChart.Series<Number, Number>> data = myChart.getData();
        if (data == null) {
            data = FXCollections.observableArrayList(series);
            myChart.setData(data);
        } else {
            myChart.getData().add(series);
        }

        myChart.autosize();

        return myChart;
    }


    public int getAantalBeursdagenKoersreeks() {
        return aantalBeursdagenKoersreeks;
    }

    private  int aantalBeursdagenKoersreeks = 0; // aantal beursdagen uit de koersreeks

    public int getEindindex() {
        return eindindex;
    }

    private  int eindindex  = 0;

    public int getStartindex() {
        return startindex;
    }

    private  int startindex = 0;
    private  String gekozenAandeel = "";
    private  CandleStickChart myChart = null;

    public ArrayList<DayPriceRecord> getMyDayPriceArray() {
        return myDayPriceArray;
    }

    private ArrayList<DayPriceRecord> myDayPriceArray = null;

    public NumberAxis getxAxis() {
        return xAxis;
    }

    //private CandleStickChart chart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private Parent chart;
    private double minPrice = 1000000.0;  // wordt bepaald aan de hand van gekozen deel van koersreeks
    private double maxPrice = 0.0;        // wordt bepaald aan de hand van gekozen deel van koersreeks

    public int getAantalBeursdagenGrafiek() {
        return aantalBeursdagenGrafiek;
    }

    private int aantalBeursdagenGrafiek = 0;

    public int getAantalDagenLinks() {
        return aantalDagenLinks;
    }

    private final int aantalDagenLinks = 1;    // aantal dagen "leeg" links in de grafiek

    public int getAantalDagenRechts() {
        return aantalDagenRechts;
    }

    private final int aantalDagenRechts = 3;   // aantal dagen "leeg" rechts in de grafiek
    private int aantalDagenRetro = 0;    // aantal dagen terug in het verleden kijken
    private ObservableList<XYChart.Series<Number, Number>> myGrafiekenschermController;

    public DayPriceRecord getLastDayDpr() {
        return lastDayDpr;
    }

    private DayPriceRecord lastDayDpr = null;

    private void init(String aGekozenAandeel,
                      int aAantalBeursdagenKoersreeks,
                      int aAantalDagenRetro)  throws Exception {
        minPrice = 100000;
        maxPrice = -100000;
        gekozenAandeel = aGekozenAandeel;
        aantalBeursdagenKoersreeks = aAantalBeursdagenKoersreeks;
        // er ZOUDEN beursdagen kunnnen ontbreken in de koersreeks, voor de eenvoud gaan we in deze
        // versie er nog vanuit dat dit niet het geval is.
        aantalBeursdagenGrafiek = aantalBeursdagenKoersreeks + aantalDagenLinks + aantalDagenRechts;
        // voor retro beleggen gaan we een aantal dagen terug in het verleden.
        // De meest recente koersen tonen we dan niet in de grafiek.
        aantalDagenRetro = aAantalDagenRetro;

        // startindex en eindindex in koersreeks
        eindindex = myDayPriceArray.size() -1 - aantalDagenRetro;   // straks nog uit te breiden met gebruik periode uit verleden
        startindex = eindindex - aantalBeursdagenKoersreeks + 1;

        if (startindex < 0)
            throw new Exception (" startindex in arraylist kleiner dan nul :" + gekozenAandeel );
        if (eindindex > myDayPriceArray.size() - 1)
            throw new Exception (" eindindex groter dan aantal koersen in arraylist");

        // Herzie zonodig de arrays met lijnen zodat die weer kloppen
        if (horLines!= null){
            for (Line line: horLines) {
                line.getP2().setX(aantalBeursdagenGrafiek);
            }
        }
         lastDayDpr = myDayPriceArray.get(eindindex);
    }


    public void panPeriod( int aantalDagenPannen) throws Exception {
        aantalDagenRetro = aantalDagenRetro - aantalDagenPannen;
        init(gekozenAandeel, aantalBeursdagenKoersreeks, aantalDagenRetro);
    }

    public void zoomUit(int aAantalBeursdagenLinksErbij) throws Exception {
        aantalBeursdagenKoersreeks += aAantalBeursdagenLinksErbij;
        init(gekozenAandeel, aantalBeursdagenKoersreeks, aantalDagenRetro);
    }
    public void zoomIn(int aAantalBeursdagenLinksErbij) throws Exception {
        aantalBeursdagenKoersreeks -= aAantalBeursdagenLinksErbij;
        init(gekozenAandeel, aantalBeursdagenKoersreeks, aantalDagenRetro);
    }




    /*
    *  Vertaal de relatieve index in de koersreeks naar de index in de grafiek
    *  koersindex = 0.. aantalBeursdagenGrafiek - 1
    */
    private int txNaarGrafiekindex(int aKoersindex) {
        int result  = aantalDagenLinks + aKoersindex;
        return result;
    }
    public Integer txNaarKoersindex(int aGrafiekindex) {
        int result = aGrafiekindex - aantalDagenLinks;
        if (result < 0)
            return null;
        if (result > aantalBeursdagenKoersreeks - 1)
            return null;
        return result;
    }
    private void vulGrafiekMetCandles(XYChart.Series<Number, Number> aSeries ) throws Exception {
        for (int i=1; i<= aantalBeursdagenGrafiek; i++) {
            if ( i <= aantalDagenLinks) {
                addDummyCandle(aSeries, i-1);
            } else if ( i >= aantalBeursdagenGrafiek - aantalDagenRechts + 1) {
                addDummyCandle(aSeries, i-1);
            } else {
                addKoersreeksCandle(aSeries, i-1);
            }
        }
    }

    private void addKoersreeksCandle(
            XYChart.Series<Number, Number> aSeries,
            int aIndex) throws Exception {
        Integer index = txNaarKoersindex(aIndex);
        if (index == null) {
            throw new Exception("FOUT addKoersreeksCandle range " + aIndex);
        }
        DayPriceRecord dpr = myDayPriceArray.get(startindex + index);
        double open, high, low, close, volume;
        open = dpr.getOpen();
        high = dpr.getHigh();
        low = dpr.getLow();
        if (low < minPrice) {
            minPrice = low;
        }
        if (high > maxPrice) {
            maxPrice = high;
        }
        close = dpr.getClose();
        volume = dpr.getVolume();
        CandleStickExtraValues extras =
                new CandleStickExtraValues(close, high, low, dpr.printDate());
        aSeries.getData().add(new XYChart.Data<Number, Number>(
                aIndex,
                dpr.getOpen(),
                extras));
    }

    private void addDummyCandle(XYChart.Series<Number, Number> aSeries, int aIndex) {
        CandleStickExtraValues dummyextras =
                new CandleStickExtraValues(-1, -1, -1, "");
        aSeries.getData().add(new XYChart.Data<Number, Number>(
                aIndex,
                -1,
                dummyextras));
    }

    /*
    *   18 feb 2020 Bij aanmaken van de graph rekening houden met het bereik van de koersreeks binnen de gekozen periode
    *
    * */


    public void addHorLine(double aY) {
        Line line = new Line(new Point(0,aY), new Point(aantalBeursdagenGrafiek,aY), startindex, aantalBeursdagenKoersreeks, lineSeqNr++);
        horLines.addLine(line);
        myChart.layoutPlotChildren();
    }

    public void delLine(Line aLine) {
        horLines.delLine(aLine.getSeqnr());
        slopedLines.delLine(aLine.getSeqnr());
    }


    // Map de lines alvorens terug te geven
    public Lines mapHorizLines() {
        Lines lines = new Lines();
        for (Line line: horLines) {
            double y = line.getP1().getY();
            lines.addLine(new Line(new Point(0, y),
                          new Point(aantalBeursdagenGrafiek, y),
                          startindex, aantalBeursdagenKoersreeks,
                          line.getSeqnr()));
        }
        return lines;
    }


    public Line mapSlopedLine(Line line) {
        double y1 = line.getP1().getY();
        double y2 = line.getP2().getY();
        int panx = startindex - line.getStartindex();
        double x1 =  line.getP1().getX() - panx;
        double x2 =  line.getP2().getX() - panx;
        double deltay = y2 - y1;
        double deltax = x2 - x1;

        double slope = deltay/deltax;
        double ylinks = y1 - x1 * slope;
        double yrechts = y1 + (aantalBeursdagenGrafiek - x1) * slope;
        return new Line(new Point(0, ylinks),
                new Point(aantalBeursdagenGrafiek, yrechts),
                startindex, aantalBeursdagenKoersreeks,
                line.getSeqnr());
    }

    // maps line to new zoom/pan position since line has been created
    // made pan proof, still to be made zoom proof
    // and to be made to stretch to graph window left and right
    public Lines mapSlopedLines() {
        Lines lines = new Lines();
        for (Line line: slopedLines) {
//            double y1 = line.getP1().getY();
//            double y2 = line.getP2().getY();
//            int panx = startindex - line.getStartindex();
//            double x1 =  line.getP1().getX() - panx;
//            double x2 =  line.getP2().getX() - panx;
//            double deltay = y2 - y1;
//            double deltax = x2 - x1;
//
//            double slope = deltay/deltax;
//            double ylinks = y1 - x1 * slope;
//            double yrechts = y1 + (aantalBeursdagenGrafiek - x1) * slope;
//
//            lines.addLine(
//                            new Line(new Point(0, ylinks),
//                                     new Point(aantalBeursdagenGrafiek, yrechts),
//                                     startindex, aantalBeursdagenKoersreeks,
//                                     line.getSeqnr()));
            lines.addLine(mapSlopedLine(line));
        }
        return lines;
    }

    public void addSlopedLine(Point firstPoint, Point secondPoint) {

        Line line = new Line(firstPoint,secondPoint, startindex, aantalBeursdagenKoersreeks, lineSeqNr++);
        slopedLines.addLine(line);
        myChart.layoutPlotChildren();
    }


    public int getCandleIxAtX(double aX) {
        if (!((aX >= aantalDagenLinks) && (aX <= aantalBeursdagenGrafiek - aantalDagenRechts - 1))) {
            System.out.println("buiten horizontaal bereik geklikt voor candles");
            return -1;
        } else {
            double px = aX - Math.floor(aX);
            int ix = 0;
            // ix is x1 rounded to nearest integer
            if (px > 0.5) {
                ix = (int) (Math.floor(aX) + 1);
            } else {
                ix = (int) Math.floor(aX);
            }
            return ix;
        }
    }

    private DayPriceRecord getCandleAtX(double aX) {
        if (!((aX >= aantalDagenLinks) && (aX <= aantalBeursdagenGrafiek - aantalDagenRechts - 1))) {
            System.out.println("buiten horizontaal bereik geklikt voor candles");
            return null;
        } else {
            double px = aX -  Math.floor(aX);
            int ix = 0;
            // ix is x1 rounded to nearest integer
            if (px > 0.5) {
                ix = (int) (Math.floor(aX) + 1);
            } else {
                ix = (int) Math.floor(aX);
            }
            Integer index = txNaarKoersindex(ix);
            DayPriceRecord dpr =  myDayPriceArray.get(startindex + index);
            return dpr;
        }
    }

    public void aanschuivenHorizLijn(double x1, double y1, Line line) {
        DayPriceRecord dpr = getCandleAtX(x1);
        if (dpr == null)
            return;

            double ylow = dpr.getLow();
            double yhigh = dpr.getHigh();
            // take ylow if y1 < hlow otherwise take yhigh

            double y = -1;
            if (y1 < ylow) {
                y = ylow;
            } else {
                y = yhigh;
            }
            if (y > 0) {  // aanschuiven op dit niveau
                    delLine(line);
                    Point p1 = line.getP1();
                    Point p2 = line.getP2();
                    Point p1_new = new Point(p1.getX(), y);
                    Point p2_new = new Point(p2.getX(), y);
                    Line newLine = new Line(p1_new, p2_new,
                            line.getStartindex(), line.getAantalBeursdagenKoersreeks(), line.getSeqnr());
                    horLines.addLine(newLine);

                myChart.layoutPlotChildren();
            } else {
                System.out.println("kon geen geschikte y positie bepalen");
            }
    }


    /*
     *     Bij het klikken in de buurt van een schuin op- of aflopende lijn worden twee niveau's getoond:
     *     het niveau en de datum en het niveau en de datum van de volgende handelsdag.
    */

    public String geefNiveaus(double aX1, Line line) {
        int ix1 = getCandleIxAtX(aX1);
        if (ix1<0)
            ix1 = 0;
        Integer index1 = txNaarKoersindex(ix1);
        if (index1 == null) {
            return "buiten candle range geklikt";
        }
        DayPriceRecord dpr1 =  myDayPriceArray.get(startindex + index1);
        String result = "datum:" + dpr1.printDate() + " gesloten op:" + dpr1.printClose() + " laag:" + dpr1.printLow();
        double x1 = (line.getP1().getX());
        double y1 = (line.getP1().getY());
        double x2 = (line.getP2().getX());
        double y2 = (line.getP2().getY());
        double y = y1 + (y2 - y1) / (x2 - x1) * (ix1 - x1);
        double yNextDay = y1 + (y2 - y1) / (x2 - x1) * (ix1 - x1 + 1);

        result += " niveau: " + String.format("%.2f", y) + " volgende niveau:" +  String.format("%.2f", yNextDay);
        return result;
    }

    public int geefDayPriceIndexAt(double x) {
        int ix = getCandleIxAtX(x);
        if (ix < 0)
            return -1;
        Integer index1 = txNaarKoersindex(ix);
        return startindex + index1;
    }

    public DayPriceRecord geefDayPriceRecordAt(Point point1) {
        int ix = getCandleIxAtX(point1.getX());
        if (ix < 0)
            return null;
        Integer index1 = txNaarKoersindex(ix);
        DayPriceRecord dpr1 =  myDayPriceArray.get(startindex + index1);
        if ((dpr1.getLow() < point1.getY()) &&
            (dpr1.getHigh() > point1.getY())) return dpr1;
        return null;
    }

    public void aanschuivenSlopedLijn(Point point1, Point point2, Line line) {
        int ix1 = getCandleIxAtX(point1.getX());
        if (ix1<0)
            return;
        int ix2 = getCandleIxAtX(point2.getX());
        if (ix2<0)
            return;
        Integer index1 = txNaarKoersindex(ix1);
        DayPriceRecord dpr1 =  myDayPriceArray.get(startindex + index1);
        Integer index2 = txNaarKoersindex(ix2);
        DayPriceRecord dpr2 =  myDayPriceArray.get(startindex + index2);

        delLine(line);
        double y1_new = -1;
        double y2_new = -1;
        if (point1.getY() < dpr1.getLow()) {
            y1_new = dpr1.getLow();
        } else {
            y1_new = dpr1.getHigh();
        }
        if (point2.getY() < dpr2.getLow()) {
            y2_new = dpr2.getLow();
        } else {
            y2_new = dpr2.getHigh();
        }
        Point p1_new = new Point(ix1, y1_new);
        Point p2_new = new Point(ix2, y2_new);
        Line newLine = new Line(p1_new, p2_new,
                line.getStartindex(), line.getAantalBeursdagenKoersreeks(), line.getSeqnr());
        slopedLines.addLine(newLine);

        myChart.layoutPlotChildren();
    }
}