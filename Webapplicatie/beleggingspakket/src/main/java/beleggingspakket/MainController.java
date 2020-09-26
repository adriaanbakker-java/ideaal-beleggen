package beleggingspakket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import beleggingspakket.Koersen.GetPriceHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

/*
*     Dit wordt de nieuwe controller van het Main window (mainwindow01.fxml)
* */


@Component
@FxmlView("mainwindow01.fxml")
public class MainController {

    public Main main;
    String pricefolder;
    ObservableList marktlijst = FXCollections.observableArrayList();
    ObservableList aandelenlijst = FXCollections.observableArrayList();
    private GetPriceHistory getPriceHistory = new GetPriceHistory();



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


    public void toonGrafiekenscherm(ActionEvent actionEvent) {
        System.out.println("Toon grafiekenscherm");
    }




    public void logInTextArea(String infostring) {
        textArea.setText(textArea.getText() + "\n" + infostring);
    }

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

        ArrayList<String> tickerSet = new ArrayList<>();
        tickerSet.addAll( getPriceHistory.getTickers() );
        Collections.sort(tickerSet);

        for (String ticker1 : tickerSet) {
            aandelenlijst.add(ticker1);
        }

        selecteerAandeel.getItems().addAll(aandelenlijst);
    }

    public void toonPortefeuille(ActionEvent actionEvent) {
        logInTextArea("toont de portefeuille");
    }

    public void koersenVerversen(ActionEvent actionEvent) {
        System.out.println("Roept via spring boot de REST service aan voor koersen verversen");
    }

    public void test() {
        System.out.println("test");
        this.lblTestlabel.setText(weatherService.getWeatherForecast());
    }

}
