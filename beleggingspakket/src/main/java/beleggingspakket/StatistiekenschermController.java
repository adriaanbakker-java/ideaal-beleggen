package beleggingspakket;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.Koersen.GetPriceHistory;
import beleggingspakket.indicatoren.ToppenEnDalen;
import beleggingspakket.portefeuillebeheer.GenereerStatistieken;
import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StatistiekenschermController implements Initializable {
    @FXML
    private Label lblHeading;
    @FXML
    private Label lblEinddatum;
    @FXML
    private Label lblBegindatum;
    @FXML
    private TextField txtMessage;
    @FXML
    private CheckBox chkKoopsignaal;
    @FXML
    private CheckBox chkGecorrMACD;
    @FXML
    private TextField txtDelta;
    @FXML
    private TextField txtBegindatum;
    @FXML
    private TextField txtEinddatum;
    @FXML
    private TextArea taLogArea;

    private String ticker;
    private ArrayList<DayPriceRecord> prices;



    private IDate begindatum;
    private IDate einddatum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize Statistiekenscherm ");
        txtDelta.setText("0.0");
    }

    // Bij het vaststellen van de ticker wordt tevens de begindatum vastgesteld
    // aan de hand van de koersfile
    public void setTicker(String aTicker) throws Exception {
        try {
            this.ticker = aTicker;
            lblHeading.setText("Statistieken voor aandeel " + aTicker);
            GetPriceHistory myGPH = new GetPriceHistory();
            prices = myGPH.getHistoricPricesFromFile(aTicker);
            setBegindatum(prices.get(0).getIDate());
        } catch (Exception e) {
            throw new Exception("StatistiekenschermController: setTicker():" + e.getLocalizedMessage());
        }
    }

    public void setBegindatum(IDate begindatum) {
        this.begindatum = begindatum;
        lblBegindatum.setText("Begindatum:" + begindatum.toString());
    }

    public void setEinddatum(IDate einddatum) {
        this.einddatum = einddatum;
        lblEinddatum.setText("Einddatum:" + einddatum.toString());
    }

    public void wijzigBegindatum() {
        System.out.println("wijzig begindatum");
        try {
            IDate dat = Util.toIDate(txtBegindatum.getText());
            this.begindatum = dat;
            lblBegindatum.setText("Begindatum:" + txtBegindatum.getText());
        } catch (Exception e) {
            addLogArea(e.getLocalizedMessage());
        }
    }

    public void wijzigEinddatum() {
        System.out.println("wijzig einddatum");
        try {
            IDate einddatumNieuw = Util.toIDate(txtEinddatum.getText());
            this.einddatum = einddatumNieuw;
            lblEinddatum.setText("Einddatum:" + txtEinddatum.getText());
        } catch (Exception e) {
            addLogArea(e.getLocalizedMessage());
        }
    }

    public void genereer() {
        try {
            boolean isKoopsignaal = chkKoopsignaal.isSelected();
            double delta = Util.toDouble(txtDelta.getText());
            String sMsg = "genereren stats koopsignaal:" + isKoopsignaal + "|" + delta;
            showMessage( sMsg);
            taLogArea.clear();
            addLogArea( sMsg );
            GenereerStatistieken stats =
                    new GenereerStatistieken(ticker, isKoopsignaal, 0, begindatum, einddatum, delta, prices);
            boolean corr = chkGecorrMACD.isSelected();
            ArrayList<String> koopverkopen = stats.berekenBeleggenMACDStoploss(corr);
            for (String msg: koopverkopen) {
                addLogArea(msg);
            }
        } catch (Exception e) {
            showMessage(e.getLocalizedMessage());
        }
    }

    public void toonLaatsteSignalen() throws Exception {
        GenereerStatistieken gen = new GenereerStatistieken(
                ticker, true, 0, begindatum, einddatum, 0, prices) ;
        String message = gen.toonLaatsteSignalen( chkGecorrMACD.isSelected());
        taLogArea.clear();
        addLogArea("Laatste 10 MAC signalen:");
        addLogArea(message);
    }

    public void toonToppenDalen() {
        try {
            System.out.println("toppen en dalen genereren");
            ToppenEnDalen td = new ToppenEnDalen(ticker, einddatum);
            td.zoekToppenEnDalen();
            addLogArea(td.toString());
        } catch (Exception e) {
            addLogArea(e.getLocalizedMessage());
        }

    }

    private void addLogArea(String sMsg) {
        taLogArea.setText(taLogArea.getText() + "\n" + sMsg);
    }

    private void showMessage(String localizedMessage) {
        txtMessage.setText(localizedMessage);
    }
}
