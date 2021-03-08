package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.grafiekenscherm.CandlestickClass;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;


    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(SpringBootExampleApplication.class)
                .run(args);
    }

    MainController mainController;
    Stage mainStage;
    Scene mainScene;
    Stage portefeuilleStage;
    Stage statistiekStage;

    Main main;
    PortefeuillebeheerController pfController;
    StatistiekenschermController stController;
    Scene pfScene;

    public void showMainWindow() {
        mainStage.show();
    }


    @Override
    public void start(Stage stage) {
        mainStage = stage;
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainController.class);

        mainController = fxWeaver.getBean(MainController.class);
        mainScene = new Scene(root);
        mainScene.getStylesheets().add("styles.css");
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        mainStage.setScene(mainScene);

        main = new Main(this);
        mainController.setMainObject(main);


        main.showMainStage();
//        stage.show();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }


    private ArrayList<DayPriceRecord> readPrices(String gekozenAandeel) {
        String infostring;
        ArrayList<DayPriceRecord> prices;

        if (gekozenAandeel == null) {
            infostring = "Eerst aandeel kiezen svp";
            log.info(infostring);
            mainController.logInTextArea(infostring);
            return null;
        } else {
            String infomsg = "lees prijsinformatie voor " + gekozenAandeel;
            log.info(infomsg);
            mainController.logInTextArea(infomsg);
            GetPriceHistory myGPH = new GetPriceHistory();
            try {
                prices = myGPH.getHistoricPricesFromFile(gekozenAandeel);
            } catch (Exception e) {
                infostring = "kon prijzen niet lezen" + e.getLocalizedMessage();
                mainController.logInTextArea(infostring);
                return null;
            }
            return prices;
        }
    }


    /*
     *  Er zijn twee versies voor het openen van een grafiekenscherm
     *     1. Vanuit de main module, met "aantal dagen retro" vanaf huidige datum
     *     2. Vanuit de portefeuille, met de einddatum uit de portefeuille
     *
     *  Het candlesticks object werkt met aantal dagen retro vanaf de laatste dagkoers.
     *  Om deze twee met elkaar te verenigen wordt het volgende ondernomen
     *
     *  Bij 1. wordt een einddatum berekend door van de huidige datum het aantal dagen retro af te trekken.
     *
     *  Bij 2. wordt de einddatum uit de portefeuille genomen
     *
     * Vervolgens wordt een effectief aantal dagen retro bepaald door te bepalen welke datum in de koersreeks nog net
     * vlak voor of op deze einddatum valt. Het effectief aantal dagen retro is als volgt bepaald:
     *
     *   eindindex (index laatst zichtbare candle)  = myDayPriceArray.size() -1 - aantalDagenRetroEffectief;
     *
     */

    // 1. Aangeroepen vanuit hoofdscherm
    public void toonGrafiekenschermRetroDagen(String gekozenAandeel, int aantalBeursdagen, IDate eindDt)
    //public void toonGrafiekenschermRetroDagen(String gekozenAandeel, int aantalBeursdagen, int aantalDagenRetro)
            throws Exception {
        //LocalDateTime eindDT = LocalDateTime.now().minusDays(aantalDagenRetro);
        //toonGrafiekenscherm(gekozenAandeel, aantalBeursdagen, Util.toIDate(eindDT));
        toonGrafiekenscherm(gekozenAandeel, aantalBeursdagen, eindDt);
    }

    // 2. Aangeroepen vanuit portefeuille
    public void toonGrafiekenschermTot(String gekozenAandeel, int aantalBeursdagen, IDate einddatum)
            throws Exception {
        toonGrafiekenscherm(gekozenAandeel, aantalBeursdagen, einddatum);
    }


    // Bepaal de index waarop de koersreeks in de grafiek moet beginnen
    private int bepaalStartindex(ArrayList<DayPriceRecord> prices, IDate einddatum) {
        int index = 1; // let op index loopt vanaf 1
        LocalDateTime ldtEinddatum = Util.toLocalDateTime(einddatum);
        // bepaal dayprice record dat in de tijd nog net op of voor de einddatum ligt
        Boolean found;

        do {
            DayPriceRecord dpr = prices.get(index - 1);
            found = !(dpr.isBefore(ldtEinddatum));
            // stoppen zodra dpr niet langer voor de beoogde einddatum ligt

            if ((!found) && (index < prices.size()))
                index++;
        } while ((!found) && (index < prices.size()));

        if (found) {
            DayPriceRecord dpr = prices.get(index - 1);
            // het kan zijn dat dpr precies op de einddatum ligt
            // het kan ook zijn dat dpr na de beoogde einddatum ligt.
            // in dat laatste geval een stapje terug

            if (!dpr.isOnDate(ldtEinddatum))
                index = index - 1;
        }
        // let op, array met prices is nul gebaseerd en index is 1 gebaseerd

        index = index - 1;
        if (index < 0 )
            return 0;
        return index;
    }

    public void toonGrafiekenscherm(String gekozenAandeel,
                                    int aantalBeursdagen,
                                    IDate einddatum) throws Exception {
       /* FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MyController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();*/
        ArrayList<DayPriceRecord> prices = readPrices(gekozenAandeel);
        int startindex = bepaalStartindex(prices, einddatum);

        Class<? extends JavaFxApplication> x = getClass();
        URL resourceURL = x.getResource("grafiekenscherm.fxml");
        FXMLLoader loader = new FXMLLoader(resourceURL);
        Parent grafiekenRoot = loader.load();
        GrafiekenschermController grafiekenschermController = loader.getController();
        grafiekenschermController.setMain(main);
        grafiekenschermController.setFondsnaam(gekozenAandeel);

        Scene grafiekenScene = new Scene(grafiekenRoot, 900, 600);
        grafiekenScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());


        Stage stage = new Stage();
        stage.setScene(grafiekenScene);
        int aantalDagenRetro = prices.size() - startindex  - 1;

        CandlestickClass candlestickClassObject = new CandlestickClass(
                prices,
                gekozenAandeel,
                aantalBeursdagen,
                aantalDagenRetro);
        grafiekenschermController.setCandleStickClassObject(candlestickClassObject);
        grafiekenschermController.createCandleChart(gekozenAandeel);
        stage.show();
    }


    private void createPortefeuilleScherm(String portefeuilleNaam) throws Exception {
        FXMLLoader loaderPF = new FXMLLoader(getClass().getResource("Portefeuillebeheer.fxml"));
        Parent pfRoot = loaderPF.load();
        pfController = loaderPF.getController();
        pfController.main = this.main;
        pfController.haalPortefeuilleVanSchijf(portefeuilleNaam);
        pfScene = new Scene(pfRoot, 800, 800);
        pfScene.getStylesheets().add("styles.css");
        main.setPfController(pfController);
        portefeuilleStage = new Stage();
        portefeuilleStage.setScene(pfScene);
    }

    public void toonStatistiekenscherm(String aandeelnaam, IDate einddatum) throws Exception {
        FXMLLoader loaderPF = new FXMLLoader(getClass().getResource("Statistieken.fxml"));
        Parent pfRoot = loaderPF.load();
        stController = loaderPF.getController();
        stController.setTicker(aandeelnaam);
        stController.setEinddatum(einddatum);
        pfScene = new Scene(pfRoot, 800, 800);
        pfScene.getStylesheets().add("styles.css");
        main.setStController(stController);
        statistiekStage = new Stage();
        statistiekStage.setScene(pfScene);
        statistiekStage.show();
    }

    public void toonPortefeuille(String pfNaam) throws Exception {
        createPortefeuilleScherm(pfNaam);
        portefeuilleStage.show();
    }


}
