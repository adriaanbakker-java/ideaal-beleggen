package beleggingspakket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import beleggingspakket.Koersen.GetPriceHistory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;


public class MainController implements Initializable {
    public Main main;


    // used to communicate between screens and to have control over the flows (regievoering)

    ObservableList marktlijst = FXCollections.observableArrayList();
    ObservableList aandelenlijst = FXCollections.observableArrayList();

    String pricefolder;
    private GetPriceHistory getPriceHistory = new GetPriceHistory();

    @FXML
    private TextField txtKoersfolder;

    @FXML
    private TextArea textArea;

    @FXML
    private ChoiceBox<String> selecteerMarkt;

    @FXML
    private ChoiceBox<String> selecteerAandeel;

    @FXML
    private ChoiceBox<String> selecteerAantalDagen;

    // retro beleggen, ga dit aantal dagen terug in verleden
    @FXML
    private ChoiceBox<String> selecteerAantalDagenVerleden;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            pricefolder = Constants.getPricefolder();
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
            txtKoersfolder.setText(Constants.getPricefolder());
        } catch (Exception e) {
            System.out.println("Maincontroller initialize:" + e.getLocalizedMessage());
        }
    }

    /*
    *               Button toon candlesticks event
    *        Obsolete. Gaat nu via toonGrafiekenScherm, via FXML scherm
    * */
//    public void candleSticksClicked(ActionEvent actionEvent) {
//        System.out.println("toon candlestick chart!");
//        String gekozenMarkt = selecteerMarkt.getValue();
//        String gekozenAandeel = selecteerAandeel.getValue();
//        int aantalKoersdagen = Integer.parseInt(selecteerAantalDagen.getValue());
//
//        int aantalDagenRetro = Integer.parseInt(selecteerAantalDagenVerleden.getValue());
//        //main.showCandlesticks(gekozenMarkt, gekozenAandeel, aantalKoersdagen, aantalDagenRetro);
//    }

    /*
     *               Button toon candlesticks event
     * */
    public void toonGrafiekenscherm(ActionEvent actionEvent) throws Exception {
        try {
            System.out.println("toon grafiekenscherm!");
            Constants.setPricefolder(txtKoersfolder.getText());
            String gekozenMarkt = selecteerMarkt.getValue();
            String gekozenAandeel = selecteerAandeel.getValue();
            int aantalKoersdagen = Integer.parseInt(selecteerAantalDagen.getValue());
            int aantalDagenRetro = Integer.parseInt(selecteerAantalDagenVerleden.getValue());
            main.toonGrafiekenscherm(gekozenMarkt, gekozenAandeel, aantalKoersdagen, aantalDagenRetro);
        } catch (Exception e) {
            logInTextArea(e.getLocalizedMessage());
        }

    }

    public  void logInTextArea(String message) {
        textArea.setText(textArea.getText() + "\n" + message);
    }

    public class LocalLogging implements LogInterface {
        @Override
        public  void printMessage(String aMessage) {
            logInTextArea(aMessage);
        }
    }


    public void koersenVerversen()  {
        Constants.setPricefolder(txtKoersfolder.getText());
        LocalLogging localLogging = new LocalLogging();
        logInTextArea("koersen verversen");
        try {
            Set<String> tickerSet = getPriceHistory.getTickers();
            for (String ticker1 : tickerSet) {
                getPriceHistory.updatePriceHistory(ticker1, Constants.startYear, 1, localLogging);
            }
        } catch (Exception e) {
            textArea.setText(e.getLocalizedMessage());
            log.error(e.getLocalizedMessage());
        }
        textArea.setText(textArea.getText() + "\n" + "koersen ververst");
    }


    public void test() {
        System.out.println("Testen!!");
        try {
            getPriceHistory.testUpdatePrice();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void toonPortefeuille() {
        main.toonPortefeuille();
    }

}
