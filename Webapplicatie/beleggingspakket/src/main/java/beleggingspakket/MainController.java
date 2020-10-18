package beleggingspakket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import beleggingspakket.Koersen.GetPriceHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/*
*     Dit wordt de nieuwe controller van het Main_backup window (mainwindow.fxml)
* */


@Component
@FxmlView("mainwindow.fxml")
public class MainController {

    public Main main;
    String pricefolder;
    ObservableList marktlijst = FXCollections.observableArrayList();
    ObservableList aandelenlijst = FXCollections.observableArrayList();
    ObservableList portefeuillelijst = FXCollections.observableArrayList();

    private GetPriceHistory getPriceHistory = new GetPriceHistory();

    public void setMainObject(Main mainObject) {
        this.main = mainObject;
        main.setMainController(this);
    }


    private WeatherService weatherService;

    @FXML
    private ChoiceBox<String> selecteerMarkt;

    @FXML
    private ChoiceBox<String> selecteerAantalDagen;

    // retro beleggen, ga dit aantal dagen terug in verleden
    @FXML
    private ChoiceBox<String> selecteerAantalDagenVerleden;

    @FXML
    private ChoiceBox<String> selecteerAandeel;

    @FXML
    private ChoiceBox<String> selecteerPortefeuille;

    @FXML
    private TextField txtEinddatum;

    @FXML
    private Label lblTestlabel;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField txtKoersenfolder;

    @Autowired
    public MainController(WeatherService weatherService) {
        this.weatherService = weatherService;
        pricefolder = Constants.getPricefolder();
    }

    private ConfigurableApplicationContext applicationContext;

    public void toonGrafiekenscherm(ActionEvent actionEvent) throws Exception {

            System.out.println("Toon grafiekenscherm");
            String gekozenMarkt = selecteerMarkt.getValue();
            String gekozenAandeel = selecteerAandeel.getValue();

            if (gekozenAandeel == null) {
                logInTextArea("Eerst aandeel kiezen svp");
            } else {
                int aantalKoersdagen = Integer.parseInt(selecteerAantalDagen.getValue());
                int aantalDagenRetro = Integer.parseInt(selecteerAantalDagenVerleden.getValue());

                logInTextArea("Vanuit maincontroller: Toon grafiekenscherm voor " + gekozenMarkt + " aandeel:"
                        + gekozenAandeel + " aantalkoersdagen " + aantalKoersdagen
                        + " aantal dagen retro" + aantalDagenRetro);
                main.toonGrafiekenscherm(gekozenMarkt, gekozenAandeel, aantalKoersdagen, aantalDagenRetro);
            }

    }




    public void logInTextArea(String infostring) {
        textArea.setText(textArea.getText() + "\n" + infostring);
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        pricefolder = Constants.getPricefolder();
        txtKoersenfolder.setText(pricefolder);

        System.out.println("loading user data for main");
        marktlijst.removeAll(selecteerMarkt);
        String a = "AEX";
        String b = "Midkap";
        String c = "Overig";
        marktlijst.addAll(a, b, c);
        selecteerMarkt.getItems().addAll(marktlijst);
        selecteerMarkt.setValue("AEX");

        selecteerAantalDagen.getItems().addAll("10", "30", "40", "60", "80");
        selecteerAantalDagen.setValue("30");
        selecteerAantalDagenVerleden.getItems().addAll("0",
                "20", "30", "60", "120", "240", "480", "720", "1440");
        selecteerAantalDagenVerleden.setValue("0");


        aandelenlijst.removeAll(selecteerAandeel);


        ArrayList<String> tickerSet = new ArrayList<>(getPriceHistory.getTickers());
        Collections.sort(tickerSet);

        tickerSet.addAll(aandelenlijst);

        selecteerAandeel.getItems().addAll(aandelenlijst);


        ArrayList<String> pfs = findPortefeuillesOnDisk();

        portefeuillelijst.clear();
        portefeuillelijst.addAll(pfs);

        portefeuillelijst.removeAll(selecteerPortefeuille);
        ArrayList<String> portefeuilles = findPortefeuillesOnDisk();
        selecteerPortefeuille.getItems().addAll(portefeuilles);

    }


    public ArrayList<String> findPortefeuillesOnDisk() {
        ArrayList<String> result = new ArrayList<>();
        result.add("Portefeuille");
        result.add("Portefeuille01");
        return result;
    }

    public void toonPortefeuille(ActionEvent actionEvent)  {
        logInTextArea("toont de portefeuille");
        try {
            String pfNaam =  selecteerPortefeuille.getValue();
            main.toonPortefeuille(pfNaam);
        } catch (Exception e) {
            logInTextArea("Exceptie bij starten portefeuille:" + e.getLocalizedMessage());
        }

    }

    public void maakPortefeuilleAan(ActionEvent actionEvent) {
        System.out.println("maak Portefeuille aan met einddatum "  + txtEinddatum.getText());
    }

    public void koersenVerversen(ActionEvent actionEvent) {
        System.out.println("Roept via spring boot de REST service aan voor koersen verversen");
    }

    public void test() {
        System.out.println("test");
        this.lblTestlabel.setText(weatherService.getWeatherForecast());
    }

}

