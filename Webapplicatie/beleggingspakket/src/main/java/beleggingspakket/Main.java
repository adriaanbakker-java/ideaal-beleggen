package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.grafiekenscherm.CandlestickClass;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class Main extends Application {
    ArrayList<GrafiekenschermController> grafiekenSchermen = new ArrayList<>();
    int grafiekenIndex = 0;
    Stage mainWindow;
    Scene mainScene;
    MainController mainController;
    GrafiekenschermController grafiekenschermController;
    Scene candlesticksSchermScene;
    Stage pfStage;

    PortefeuillebeheerController pfController;
    Scene pfScene;


    @Override
    public void start(Stage stage) throws IOException {
        mainWindow = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
        Parent mainroot = loader.load();
        mainController = loader.getController();
        mainController.main = this;

        mainScene = new Scene(mainroot, 800, 500);
        mainWindow.setScene(mainScene);
        mainWindow.show();

        FXMLLoader loaderPF = new FXMLLoader(getClass().getResource("Portefeuillebeheer.fxml"));
        Parent pfRoot = loaderPF.load();
        pfController = loaderPF.getController();
        pfController.main = this;

        pfScene = new Scene(pfRoot, 800, 800);

    }

    public static void main(String[] args) {
        launch(args);
    }

    private ArrayList<DayPriceRecord> prepareCandleSticks(String gekozenMarkt, String gekozenAandeel, int aantalBeursdagen,
                                                          int aantalDagenRetro) {
        String infostring;
        ArrayList<DayPriceRecord> prices;

        if (gekozenMarkt == null) {
            infostring = "Eerst markt kiezen svp";
            log.info(infostring);
            mainController.logInTextArea(infostring);
            return null;
        } else if (gekozenAandeel == null) {
            infostring = "Eerst aandeel kiezen svp";
            log.info(infostring);
            mainController.logInTextArea(infostring);
            return null;
        } else {
            infostring = "showCandleSticks aangeroepen voor markt " + gekozenMarkt +
                    " en aandeel " + gekozenAandeel +
                    " aantal beursdagen is " + aantalBeursdagen +
                    " aantal dagen in verleden is " + aantalDagenRetro;
            log.info(infostring);
            mainController.logInTextArea(infostring);
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
     *   Toon grafiekenscherm, via fxml opgebouwd scherm
     * */
    public void toonGrafiekenscherm(String gekozenMarkt, String gekozenAandeel, int aantalBeursdagen,
                                    int aantalDagenRetro) throws Exception {
        ArrayList<DayPriceRecord> prices = null;
        try {
            prices = prepareCandleSticks(gekozenMarkt, gekozenAandeel, aantalBeursdagen, aantalDagenRetro);
            if (prices == null)
                throw new Exception("koersbestand niet gevonden of leeg");
            if (prices.size() <= 10 )
                throw new Exception("koersbestand niet gevonden of leeg");
        } catch (Exception e) {
            throw new Exception("toonGrafiekenscherm:" + e.getLocalizedMessage());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("grafiekenscherm.fxml"));
        Parent grafiekenRoot = loader.load();
        grafiekenschermController = loader.getController();
        grafiekenschermController.main = this;
        grafiekenschermController.setFondsnaam(gekozenAandeel);

        candlesticksSchermScene = new Scene(grafiekenRoot, 900, 600);

        // Bij meerdere gesynchroniseerde schermen moet een array worden bijgehouden
        // met actieve grafiekenschermen (= stages).
        Stage candleStage = new Stage();
        candleStage.setScene(candlesticksSchermScene);
        CandlestickClass candlestickClassObject = new CandlestickClass(
                prices,
                gekozenAandeel,
                aantalBeursdagen,
                aantalDagenRetro);


        grafiekenschermController.setCandleStickClassObject(candlestickClassObject);

        // Set het chart object in de grafiekenschermController en zet tevens in dat
        // chart object de referentie naar de grafiekenschermController (dus wederzijdse link).

        grafiekenschermController.createCandleChart(gekozenAandeel);

        grafiekenSchermen.add(grafiekenschermController);

        grafiekenschermController.setIndex(grafiekenIndex);
        int currentCounter = grafiekenIndex++;
        candleStage.setOnHiding(event -> closeGrafiekenscherm(currentCounter));
        candleStage.show();
    }

    private void closeGrafiekenscherm(int currentCounter) {
        grafiekenSchermen.remove(currentCounter);
    }


    public void toonMessage(String aMessage) {
        mainController.logInTextArea(aMessage);
    }

    public void toonPortefeuille() {
        System.out.println("main program: show portefeuille");
        if (pfStage == null) {
            pfStage = new Stage();
            pfStage.setScene(pfScene);
        }

        pfStage.show();
    }

    // Process the orders that are outstanding by confronting them with the dpr
    public boolean verwerkOrders(DayPriceRecord dpr) {
        boolean result = false;
        try {
            result = pfController.verwerkOrders(dpr);
            pfController.processMatchedOrders();
            pfController.addOrdersToScreen();
            pfController.addTransactionsToScreen();
            pfController.addPositionsToScreen(dpr.getYear(),
                    dpr.getMonth(), dpr.getDay());
            pfController.toonOrders();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return result;
    }


    /*
    *  When in grafiekenscherm the option is clicked to move one tradingday to the right
    *  (the retro trading button) all OTHER grafiekenschermen move to the right this one
    *  tradingday. This happens when the orders have already been processed.
    */
    public void beursdagNaarRechts(int grafiekenIndex) {
        for (GrafiekenschermController gc: grafiekenSchermen) {
            if (gc.getGrafiekenIndex() != grafiekenIndex) {
                gc.beursdagNaarRechts();
            }
        }
    }
}
