package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.indicatoren.*;
import beleggingspakket.util.IDate;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

import static java.time.LocalDateTime.now;

public class Main {

    private JavaFxApplication app;
    private MainController mainController;
    private PortefeuillebeheerController pfController = null;
    private StatistiekenschermController stController;

    public Main(JavaFxApplication javaFxApplication) {
        this.app = javaFxApplication;
    }

    public void showMainStage() {
        app.showMainWindow();
    }

    public void toonGrafiekenscherm(
            String gekozenMarkt,
            String gekozenAandeel,
            int aantalKoersdagen,
            int aantalDagenRetro) throws Exception {
        System.out.println("Vanuit Main: Toon grafiekenscherm");
        mainController.logInTextArea("Vanuit Main: Toon GrafiekenschermController");
        app.toonGrafiekenschermRetroDagen(gekozenAandeel, aantalKoersdagen, aantalDagenRetro);
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setPfController(PortefeuillebeheerController aPfController) {
        this.pfController = aPfController;
    }
    public void setStController(StatistiekenschermController stController) {
        this.stController = stController;
    }

    public void toonPortefeuille(String pfNaam) throws Exception {
        app.toonPortefeuille(pfNaam);
    }

    // Process the orders that are outstanding by confronting them with the dpr
    public boolean verwerkOrders(DayPriceRecord dpr) {
        boolean result = false;
        try {
            result = pfController.verwerkOrders(dpr);
            pfController.processMatchedOrders();
            pfController.addOrdersToScreen();
            pfController.addTransactionsToScreen();
            String positionMessage = pfController.addPositionsToScreen(dpr.getYear(),
                    dpr.getMonth(), dpr.getDay());
           pfController.toonOrders();

            if (!positionMessage.equals("")) {
                pfController.showMessage(positionMessage);
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return result;
    }


    public void toonGrafiekenschermTot(String gekozenAandeel, int aantalKoersdagen, IDate einddatum) throws Exception {
        app.toonGrafiekenschermTot( gekozenAandeel,  aantalKoersdagen,  einddatum);
    }

    public void beursdagNaarRechts(IDate lastDate) {
        pfController.beursdagNaarRechts(lastDate);
    }

    public void logInTextArea(String logmessage) {
        mainController.logInTextArea(logmessage);
    }

    public void toonSignalen(String ticker) {
        try {
            IDate date = new IDate(now());
            checkSignalen(ticker, date);
        } catch (Exception e) {
            logInTextArea(e.getLocalizedMessage());
        }

    }

    String listLaatsteSignaal(ArrayList<IndicatorSignal> signalen, IDate aDate) {
        IndicatorSignal lastSignal = null;

        for (IndicatorSignal sig: signalen) {
            if (sig.getDate().isSmallerEqual(aDate)) {
                lastSignal = sig;
            }
        }
        String sResult = "";
        if (lastSignal != null) {
            sResult =     "koop    ";
            if (!lastSignal.getKoopsignaal())
                sResult = "verkoop ";
            sResult += lastSignal.getDate().toString();
        }
        return sResult;
    }

    private String checkSignal(String aIndicatorNaam, Indicator aIndicator, IDate aDate) {
        String sSignaal = listLaatsteSignaal(aIndicator.getSignals(), aDate);
        String sResult = "";
        if (!sSignaal.equals(""))
            sResult += aIndicatorNaam + ":" + sSignaal;
        return sResult;
    }

    public void checkSignalen(String ticker, IDate aDate) throws Exception {
        GetPriceHistory myGPH = new GetPriceHistory();
        ArrayList<DayPriceRecord> prices;
        prices = myGPH.getHistoricPricesFromFile(ticker);
        try {
            logInTextArea(ticker + " signalen:");
            String sResult = checkSignal("MACD", new MACD(prices), aDate);
            sResult += checkSignal(" OBV", new OnBalanceVolume(prices), aDate);
            sResult += checkSignal(" MOM", new Momentum(prices), aDate);
            sResult += checkSignal(" RSI", new RSI(prices), aDate);

            logInTextArea(sResult);
        } catch (Exception e) {
            throw new Exception (e.getLocalizedMessage());
        }
    }

    public void toonStatistieken(String gekozenAandeel, IDate einddatum) throws Exception {
        app.toonStatistiekenscherm(gekozenAandeel, einddatum);
    }

}
