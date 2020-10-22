package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main {

    private JavaFxApplication app;
    private MainController mainController;
    private PortefeuillebeheerController pfController = null;

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
            pfController.addPositionsToScreen(dpr.getYear(),
                    dpr.getMonth(), dpr.getDay());
            pfController.toonOrders();
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
}
