package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.grafiekenscherm.CandleStickChart;
import beleggingspakket.grafiekenscherm.CandlestickClass;
import beleggingspakket.grafiekenscherm.Line;
import beleggingspakket.grafiekenscherm.Point;
import beleggingspakket.indicatoren.*;
import beleggingspakket.util.IDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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

    private String fondsnaam = "nog niet ingevuld";
    private Point point1 = null;
    private Point point2 = null;

    @FXML
    private Label lblFondsnaam;

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

    @FXML
    private ChoiceBox<String> cmbBeursdagen;

    @FXML
    private Label lblMessage;

    private Main main;
    private LineChart<Number, Number> macdChart = null;
    private LineChart<Number, Number> OBVChart = null;
    private LineChart<Number, Number> RSIChart = null;
    private LineChart<Number, Number> MomentumChart = null;
    private BarChart<Number, Number> volumeChart = null;

    private CandlestickClass myCandlestickObject = null;
    private CandleStickChart myCandleStickChart = null;

    public enum EventState {
        state_idle,
        state_wait_horline,
        state_wait_slopedline,
        state_wait_slopedline2,
        state_wait_delline,
        state_wait_schuifaan_2, state_wait_schuifaan_1
    }

    private Point firstPoint;

    private EventState eventState = EventState.state_idle;

    private class DayPriceRecordPlusIndex {
        int index;
        DayPriceRecord dpr;

        public DayPriceRecordPlusIndex(int index, DayPriceRecord dpr) {
            this.index = index;
            this.dpr = dpr;
        }
    }

    private DayPriceRecordPlusIndex geefDayPriceRecordAt(Number x1, Number y1) {
        double xd = (double) x1;
        double yd = (double) y1;
        if (   (xd - x1.intValue() < 0.1)
                || (xd - (x1.intValue() + 1) < 0.1)) {

            Integer index1 = myCandlestickObject.geefDayPriceIndexAt(xd);
            if (index1 < 0)
                return null;

            DayPriceRecordPlusIndex result =
                    new DayPriceRecordPlusIndex(
                            index1,
                            myCandlestickObject.getMyDayPriceArray().get(index1));
            return result;
        }

        return null;
    }

    @Autowired
    public GrafiekenschermController() {
    }


    public void loadWeatherForecast(ActionEvent actionEvent) {
        WeatherService weatherService = new WeatherService();
        this.weatherLabel.setText(weatherService.getWeatherForecast());
    }

    public void toonToppenDalen() {
        System.out.println("Toon toppen en dalen");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbBeursdagen.getItems().addAll("1", "5", "20", "40", "60");
        cmbBeursdagen.setValue("1");
    }

    public void setFondsnaam(String gekozenAandeel) {
        lblFondsnaam.setText(gekozenAandeel);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setAandeel(String gekozenAandeel) {
        weatherLabel.setText(gekozenAandeel);
    }

    public void zoomIn() {
        System.out.println("zoom in");
        try {
            int aantalDagen  = Integer.parseInt(cmbBeursdagen.getValue());
            toonMessage("zoom in, " + aantalDagen +" dag(en) links eraf");
            myCandlestickObject.zoomIn(aantalDagen);
            createCandleChart(fondsnaam);
        } catch (Exception e) {
            toonMessage("kan niet verder inzoomen");
        }
    }

    private void toonMessage(String s) {
        this.lblMessage.setText(s);
    }

    public void zoomUit() {  // 5 beursdagen naar links uitzoomen
        try {
            int aantalDagen  = Integer.parseInt(cmbBeursdagen.getValue());
            toonMessage("zoom uit, " + aantalDagen +" dag(en) links erbij");
            myCandlestickObject.zoomUit(aantalDagen);
            createCandleChart(fondsnaam);
        } catch (Exception e) {
            toonMessage("kan niet verder uitzoomen");
        }
    }


    public void beursdagNaarLinks()  {
        try {
            int aantalDagen  = Integer.parseInt(cmbBeursdagen.getValue());
            toonMessage(aantalDagen + " beursdag(en) naar links");
            myCandlestickObject.panPeriod(-aantalDagen);
            createCandleChart(fondsnaam);
        } catch (Exception e) {
            toonMessage("kan niet verder naar links");
        }
    }



    public void beursdagNaarRechts()  {
        try {
            int aantalDagen  = Integer.parseInt(cmbBeursdagen.getValue());
            toonMessage(aantalDagen + " beursdag(en) naar rechts");
            IDate lastDate = myCandlestickObject.panPeriod(aantalDagen);
            createCandleChart(fondsnaam);

            main.beursdagNaarRechts(lastDate);

        } catch (Exception e) {
            toonMessage("kan niet verder naar rechts");
        }
    }

    public void addHorLine() {
        System.out.println("klik in de grafiek");
        eventState = EventState.state_wait_horline;
    }

    public void delHorLine() {
        System.out.println("klik in grafiek");
        eventState = EventState.state_wait_delline;
    }

    public void addSlopedLine() {
        toonMessage("klik in de grafiek voor eerste punt");
        eventState = EventState.state_wait_slopedline;
    }
    public void schuifHorLijnAan() {
        toonMessage("Klik op een lijn");
        eventState = EventState.state_wait_schuifaan_1;
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
                    momentum.getIndicatorLine(),
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
                    obv.getIndicatorLine(),
                    obv.getIndicatorMALine(),
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
            macd = new MACD(myCandlestickObject.getMyDayPriceArray(), false);
            IndicatorClass indicator = new IndicatorClass(
                    xAxis,
                    yAxis,
                    macd.getIndicatorLine(),
                    macd.getMACDSmoothed(),
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

    // Indien er orders zijn ingelegd, check of je ze kunt uitvoeren
    private boolean checkUitvoerenOrders(DayPriceRecord dpr) {
        System.out.println("check orders uitvoeren");
        return main.verwerkOrders(dpr);
    }

    public void verwerkEenBeursdag()  {
        try {
            int lastIndex = myCandlestickObject.getEindindex();
            ArrayList<DayPriceRecord> priceArray = myCandlestickObject.getMyDayPriceArray();
            int endIndex = myCandlestickObject.getMyDayPriceArray().size() - 1;
            if (lastIndex + 1 <= endIndex) {
                DayPriceRecord dpr = priceArray.get(lastIndex + 1);
                boolean uitgevoerd = checkUitvoerenOrders(dpr);
                if (uitgevoerd) {
                    toonMessage( "Er zijn order(s) uitgevoerd");
                } else {
                    toonMessage("geen uitgevoerde orders");
                }
                //myCandlestickObject.panPeriod(1);
                createCandleChart(fondsnaam);
                beursdagNaarRechts();
            } else {
                toonMessage("Aan einde van beschikbare koersen");
            }


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());

            toonMessage("verwerkEenBeursdag: kan niet verder naar rechts");
        }
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

    public void lineClicked(Number x1, Number y1, Line line, boolean aIsHorizontaal) {
        double yVal = line.getP1().getY();
        String sVal = String.format("%.2f", yVal);
        toonMessage("in de buurt van lijn geklikt:" + sVal);
        if (aIsHorizontaal)
            lblMessage.setText(sVal);

        String sLine = "";
        if (eventState == EventState.state_idle) {
            if (!aIsHorizontaal) {
                lblMessage.setText(myCandlestickObject.geefNiveaus((Double) x1, line));
            }
        } else
        if (eventState == EventState.state_wait_delline) {
            if (aIsHorizontaal) {
                sLine = "horiz. lijn verwijderen";
            } else {
                sLine += "schuine lijn verwijderen";
            }
            toonMessage(sLine);
            double y = (double) y1;
            myCandlestickObject.delLine(line);
            myCandleStickChart.layoutPlotChildren();
            eventState = EventState.state_idle;
        } else if (eventState == EventState.state_wait_schuifaan_1){
            if (aIsHorizontaal) {
                aanschuivenHorizLijn( (double) x1, (double) y1,  line);
                eventState = EventState.state_idle;
            } else {
                point1 = new Point((double) x1, (double) y1);
                sLine = "schuine lijn aanschuiven - klik op tweede punt op de lijn";
                toonMessage(sLine);
                eventState = EventState.state_wait_schuifaan_2;
            } // aanschuiven
        } else if (eventState == EventState.state_wait_schuifaan_2){
            point2 = new Point((double) x1, (double) y1);
            aanschuivenSlopedLine(point1, point2, line);
            eventState = EventState.state_idle;
        }
    }

    private void aanschuivenSlopedLine(Point point1, Point point2, Line line) {
        String sLine = "controller schuine lijn aanschuiven - tweede punt geklikt, aanschuiven nog implementeren";
        toonMessage(sLine);
        myCandlestickObject.aanschuivenSlopedLijn(point1, point2, line);
    }


    private void aanschuivenHorizLijn(double x1, double y1, Line line) {
        String sLine;
        sLine = "horiz. lijn aanschuiven";
        toonMessage(sLine);
        myCandlestickObject.aanschuivenHorizLijn(x1, y1, line);
    }


    public void mouseClick(Number x1, Number y1) {
        if (eventState == EventState.state_idle) {
            toonMessage("Controller: geklikt op:" + x1 + "," + y1);
            DayPriceRecordPlusIndex dprpi = geefDayPriceRecordAt(x1, y1);
            if (dprpi != null) {
                CalcVolatility calcVolatility = new CalcVolatility(myCandlestickObject.getMyDayPriceArray());
                toonMessage("candle clicked:" + dprpi.dpr.toString() + " at index " +
                        dprpi.index + " volatily(250 days)=" +
                        calcVolatility.calcVolatility(dprpi.index));

            }

        } else if (eventState == EventState.state_wait_horline) {
            System.out.println("teken horizontale lijn op prijs " + y1);
            double y = (double) y1;
            myCandlestickObject.addHorLine(y);
            eventState = EventState.state_idle;
        } else if ((eventState == EventState.state_wait_delline) ||
                (eventState == EventState.state_wait_schuifaan_1) ||
                (eventState == EventState.state_wait_schuifaan_2)){
            toonMessage("klik nog eens in de buurt van de lijn");
        } else if (eventState == EventState.state_wait_slopedline) {
            toonMessage("klik voor het tweede punt van de lijn");
            double x = (double) x1;
            double y = (double) y1;
            firstPoint = new Point(x,y);
            eventState = EventState.state_wait_slopedline2;
        } else if (eventState == EventState.state_wait_slopedline2) {
            toonMessage("tweede punt geklikt");
            double x = (double) x1;
            double y = (double) y1;
            Point secondPoint = new Point(x,y);

            myCandlestickObject.addSlopedLine(firstPoint, secondPoint);
            eventState = EventState.state_idle;
        } else {
            toonMessage("mouseclick maar weet niet hoe deze te verwerken in toestand " + eventState);
        }
    }

}