package beleggingspakket;

import beleggingspakket.grafiekenscherm.CandleStickChart;
import beleggingspakket.grafiekenscherm.CandlestickClass;
import beleggingspakket.grafiekenscherm.Line;
import beleggingspakket.indicatoren.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//@Component
//@FxmlView("grafiekenscherm.fxml")
public class GrafiekenschermController implements Initializable {

    @FXML
    private Label weatherLabel;

    @FXML
    private CheckBox chkMACD;

    @FXML
    private CheckBox chkVolume;

    @FXML
    private CheckBox chkOBV;

    @FXML
    private CheckBox chkRSI;

    @FXML
    private CheckBox chkMomentum;

    @FXML
    private BorderPane borderPane;

    private LineChart<Number, Number> macdChart = null;
    private LineChart<Number, Number> OBVChart = null;
    private LineChart<Number, Number> RSIChart = null;
    private LineChart<Number, Number> MomentumChart = null;
    private BarChart<Number, Number> volumeChart = null;

    private Main main;
    private CandlestickClass myCandlestickObject = null;
    private CandleStickChart myCandleStickChart = null;

    @Autowired
    public GrafiekenschermController() {
    }


    public void loadWeatherForecast(ActionEvent actionEvent) {
        WeatherService weatherService = new WeatherService();
        this.weatherLabel.setText(weatherService.getWeatherForecast());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void setMain(Main main) {
        this.main = main;
    }

    public void setAandeel(String gekozenAandeel) {
        weatherLabel.setText(gekozenAandeel);
    }

    public void zoomIn() {
        System.out.println("zoom in");
    }

    public void zoomUit() {
        System.out.println("zoom uit");
    }

    public void beursdagNaarLinks() {
        System.out.println("beursdagNaarLinks");
    }

    public void beursdagNaarRechts() {
        System.out.println("beursdagNaarRechts");
    }

    public void addHorLine() {
        System.out.println("addHorLine");
    }

    public void delHorLine() {
        System.out.println("delHorLine");
    }

    public void addSlopedLine() {
        System.out.println("addSlopedLine");
    }
    public void schuifHorLijnAan() {
        System.out.println("schuifHorLijnAan");
    }

    /*
     *  Bepaalt de relatieve grootte van de vensters:
     *    - CandlestickChart die altijd wordt getoond
     *    - eventuele indicatoren die eronder worden getoond
     *
     *  Plaats deze ieder in een eigen venster boven elkaar.
     *
     *  Alleen candlesticks - 100%
     *  Ieder volgende indicator
     *    candlesticks 50%
     *    rest verdeeld over
     *       N = aantal aangevinkte indicatoren
     *       iedere volgende indicator krijgt 50%/N
     *
     *
     */
    public void putChartsOnScreen() {
        double prefHeight = 1000;

        double size_candles = 1.0;
        double size_MACD = 0;
        double size_volume = 0;
        double size_rest = 0;
        VBox vbox = new VBox();


        int nIndicatoren = 0;
        if (chkMACD.isSelected()) nIndicatoren++;
        if (chkOBV.isSelected()) nIndicatoren++;
        if (chkVolume.isSelected()) nIndicatoren++;
        if (chkRSI.isSelected()) nIndicatoren++;
        int percCandles = 100;
        int percRest = 0;
        double verticalSpaceIndicator = 0;

        if (nIndicatoren > 0 ) {
            percCandles = 50;
            percRest = (100 - percCandles)/nIndicatoren;
            verticalSpaceIndicator = prefHeight * percRest / 100;
        }

        // Verdeel de beschikbare ruimte vertikaal over de candlesticks en de indicatoren
        // Wijs voor iedere chart ruimte toe in de VBox na deze te hebben aangemaakt

        myCandleStickChart.setPrefHeight(prefHeight * percCandles/100);
        vbox.getChildren().add(myCandleStickChart);

        if (chkMACD.isSelected()) {
            macdChart = createMACD();
            macdChart.setPrefHeight(verticalSpaceIndicator);
            vbox.getChildren().add(macdChart);
        }

        if (chkOBV.isSelected()) {
            OBVChart = createOBV();
            OBVChart.setPrefHeight(verticalSpaceIndicator);
            vbox.getChildren().add(OBVChart);
        }

        if (chkVolume.isSelected()) {
            volumeChart = createBarchart();
            volumeChart.setPrefHeight(verticalSpaceIndicator);
            vbox.getChildren().add(volumeChart);
        }

        if (chkRSI.isSelected()) {
            RSIChart = createRSI();
            RSIChart.setPrefHeight(verticalSpaceIndicator);
            vbox.getChildren().add(RSIChart);
        }

        if (chkMomentum.isSelected()) {
            MomentumChart = createMomentum();
            MomentumChart.setPrefHeight(verticalSpaceIndicator);
            vbox.getChildren().add(MomentumChart);
        }
        borderPane.setCenter(vbox);
    }

    private LineChart<Number, Number> createMomentum() {
        NumberAxis xAxis = null;
        NumberAxis yAxis = null;
//        List<Double> values = new ArrayList<>();

        xAxis = myCandlestickObject.getxAxis();
        yAxis = new NumberAxis();

        int iStartindex = myCandlestickObject.getStartindex();
        int iEindindex = myCandlestickObject.getEindindex();

        // Vul het values array met waarden
        Momentum momentum = null;
        try {
            momentum = new Momentum(myCandlestickObject.getMyDayPriceArray());
            IndicatorClass indicator = new IndicatorClass(
                    xAxis,
                    yAxis,
                    momentum.MomentumLine(),
                    null,
                    "Momentum",
                    iStartindex,
                    iEindindex,
                    myCandlestickObject.getAantalDagenLinks(),
                    true);
            //borderPane.setBottom(indicator.getChart());
            int  xAxisWidth =
                    myCandlestickObject.getAantalBeursdagenGrafiek();
            return indicator.getLineChart();
        } catch (Exception e) {
            System.out.println("FOUT in createMomentum():" + e.getLocalizedMessage());
            return null;
        }
    }


    private LineChart<Number, Number> createRSI() {
        NumberAxis xAxis = null;
        NumberAxis yAxis = null;
//        List<Double> values = new ArrayList<>();

        xAxis = myCandlestickObject.getxAxis();
        yAxis = new NumberAxis();

        int iLinks = myCandlestickObject.getAantalDagenLinks();
        int iRechts = myCandlestickObject.getAantalDagenRechts();
        int iAantal = myCandlestickObject.getAantalBeursdagenKoersreeks();
        int iStartindex = myCandlestickObject.getStartindex();
        int iEindindex = myCandlestickObject.getEindindex();

        // Vul het values array met waarden
        RSI rsi = null;
        try {
            rsi = new RSI(myCandlestickObject.getMyDayPriceArray());
            IndicatorClass indicator = new IndicatorClass(
                    xAxis,
                    yAxis,
                    rsi.getRSILine(),
                    null,
                    "RSI",
                    iStartindex,
                    iEindindex,
                    myCandlestickObject.getAantalDagenLinks(),
                    true);
            //borderPane.setBottom(indicator.getChart());
            int  xAxisWidth =
                    myCandlestickObject.getAantalBeursdagenGrafiek();
            return indicator.getRSIChart(xAxisWidth);
        } catch (Exception e) {
            System.out.println("FOUT in createRSI():" + e.getLocalizedMessage());
            return null;
        }
    }


    private BarChart<Number, Number> createBarchart() {
        NumberAxis xAxis = null;
        NumberAxis yAxis = null;
//        List<Double> values = new ArrayList<>();

        xAxis = myCandlestickObject.getxAxis();
        yAxis = new NumberAxis();

        int iLinks = myCandlestickObject.getAantalDagenLinks();
        int iRechts = myCandlestickObject.getAantalDagenRechts();
        int iAantal = myCandlestickObject.getAantalBeursdagenKoersreeks();
        int iStartindex = myCandlestickObject.getStartindex();
        int iEindindex = myCandlestickObject.getEindindex();

        // Vul het values array met waarden
        ArrayList<Double> volumes = new ArrayList<>();
        try {
            for (int i=iStartindex; i<=iEindindex; i++) {
                int volume = myCandlestickObject.getMyDayPriceArray().get(i).getVolume();
                Double dVolume = Double.valueOf(volume);
                volumes.add(dVolume);
            }

            //volumes = new MACD(myCandlestickObject.getMyDayPriceArray());
            BarchartIndicator barchartIndicator = new BarchartIndicator(
                    this.myCandlestickObject,
                    xAxis,
                    yAxis,
                    volumes,
                    "Volumes",
                    myCandlestickObject.getAantalDagenLinks(),
                    myCandlestickObject.getAantalDagenRechts());
            //borderPane.setBottom(indicator.getChart());
            return barchartIndicator.getChart();
        } catch (Exception e) {
            System.out.println("FOUT in createBarchart():" + e.getLocalizedMessage());
            return null;
        }
    }


    private LineChart<Number, Number>  createOBV() {
        NumberAxis xAxis = null;
        NumberAxis yAxis = null;
//        List<Double> values = new ArrayList<>();

        xAxis = myCandlestickObject.getxAxis();
        yAxis = new NumberAxis();

        int iLinks = myCandlestickObject.getAantalDagenLinks();
        int iRechts = myCandlestickObject.getAantalDagenRechts();
        int iAantal = myCandlestickObject.getAantalBeursdagenKoersreeks();
        int iStartindex = myCandlestickObject.getStartindex();
        int iEindindex = myCandlestickObject.getEindindex();

        // Vul het values array met waarden
        OnBalanceVolume obv = null;
        try {
            obv = new OnBalanceVolume(myCandlestickObject.getMyDayPriceArray());
            IndicatorClass indicator = new IndicatorClass(
                    xAxis,
                    yAxis,
                    obv.getOBVline(),
                    obv.getOBVMovingAverage(),
                    "On Balance Volume",
                    iStartindex,
                    iEindindex,
                    myCandlestickObject.getAantalDagenLinks(),
                    false);
            //borderPane.setBottom(indicator.getChart());
            return indicator.getLineChart();
        } catch (Exception e) {
            System.out.println("FOUT in createOBV():" + e.getLocalizedMessage());
            return null;
        }
    }


    private LineChart<Number, Number>  createMACD() {
        NumberAxis xAxis = null;
        NumberAxis yAxis = null;
//        List<Double> values = new ArrayList<>();

        xAxis = myCandlestickObject.getxAxis();
        yAxis = new NumberAxis();

        int iLinks = myCandlestickObject.getAantalDagenLinks();
        int iRechts = myCandlestickObject.getAantalDagenRechts();
        int iAantal = myCandlestickObject.getAantalBeursdagenKoersreeks();
        int iStartindex = myCandlestickObject.getStartindex();
        int iEindindex = myCandlestickObject.getEindindex();

        // Vul het values array met waarden
        MACD macd = null;
        try {
            macd = new MACD(myCandlestickObject.getMyDayPriceArray());
            IndicatorClass indicator = new IndicatorClass(
                    xAxis,
                    yAxis,
                    macd.getMACDlist(),
                    macd.getMACDSignal(),
                    "MACD",
                    iStartindex,
                    iEindindex,
                    myCandlestickObject.getAantalDagenLinks(),
                    true);
            //borderPane.setBottom(indicator.getChart());
            return indicator.getLineChart();
        } catch (Exception e) {
            System.out.println("FOUT in createMACD():" + e.getLocalizedMessage());
            return null;
        }
    }


    public void toonPortefeuille() {
        System.out.println("toonPortefeuille");
    }

    public void verwerkEenBeursdag() {
        System.out.println("verwerkEenBeursdag");
    }

    public void setCandleStickClassObject(CandlestickClass candlestickClassObject) {
        this.myCandlestickObject = candlestickClassObject;
    }


    public void createCandleChart(String gekozenAandeel) throws Exception {
        myCandleStickChart  =  myCandlestickObject.createContent(gekozenAandeel);
        myCandleStickChart.setLegendVisible(false);
        myCandleStickChart.setPrefHeight(1000);
        myCandleStickChart.setMaxHeight(1000);
        putChartsOnScreen();
        myCandleStickChart.setMyGrafiekenschermController(this);
    }

    public void lineClicked(double x1, double y1, Line line, boolean b) {
    }

    public void mouseClick(Number x1, Number y1) {
    }
}