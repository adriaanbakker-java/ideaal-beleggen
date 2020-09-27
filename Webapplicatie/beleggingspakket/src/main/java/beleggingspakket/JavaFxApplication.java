package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.grafiekenscherm.CandlestickClass;
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
    Main main;

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



    public void toonGrafiekenscherm(String gekozenMarkt,
                                    String gekozenAandeel,
                                    int aantalBeursdagen,
                                    int aantalDagenRetro) throws Exception {
       /* FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MyController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();*/
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


        Class<? extends JavaFxApplication> x = getClass();
        URL resourceURL = x.getResource("grafiekenscherm.fxml");
        FXMLLoader loader = new FXMLLoader(resourceURL);
        Parent grafiekenRoot = loader.load();
        GrafiekenschermController grafiekenschermController = loader.getController();
        grafiekenschermController.setMain(main);
        grafiekenschermController.setAandeel(gekozenAandeel);

        Scene grafiekenScene = new Scene(grafiekenRoot, 900, 600);

        Stage stage = new Stage();
        stage.setScene(grafiekenScene);
        CandlestickClass candlestickClassObject = new CandlestickClass(
                prices,
                gekozenAandeel,
                aantalBeursdagen,
                aantalDagenRetro);
        grafiekenschermController.setCandleStickClassObject(candlestickClassObject);
        grafiekenschermController.createCandleChart(gekozenAandeel);

        stage.show();
    }


}
