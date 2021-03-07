package beleggingspakket;

import beleggingspakket.indicatoren.ToppenEnDalen;
import beleggingspakket.portefeuillebeheer.GenereerStatistieken;
import beleggingspakket.portefeuillebeheer.StatistiekUitkomst;
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
    private TextField txtMessage;
    @FXML
    private CheckBox chkKoopsignaal;
    @FXML
    private TextField txtDelta;
    @FXML
    private TextField txtEinddatum;
    @FXML
    private TextField txtNDagen;
    @FXML
    private TextArea taLogArea;

    private String ticker;
    private IDate einddatum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize Statistiekenscherm ");
        txtDelta.setText("0.0");
    }

    public void setTicker(String aTicker) {
        this.ticker = aTicker;
        lblHeading.setText("Statistieken voor aandeel " + aTicker);
    }

    public void setEinddatum(IDate einddatum) {
        this.einddatum = einddatum;
        lblEinddatum.setText("Einddatum:" + einddatum.toString());
    }


    public void genereerStatistiekenMACD() throws Exception {
        boolean isKoopsignaal = chkKoopsignaal.isSelected();
        double delta = Util.toDouble(txtDelta.getText());
        String sMsg = "genereren stats koopsignaal:" + isKoopsignaal + "|" + delta;
        showMessage( sMsg);
        taLogArea.clear();
        addLogArea( sMsg );

        for (int n=1; n<=10; n++) {
            GenereerStatistieken stats = new GenereerStatistieken(ticker, isKoopsignaal, n, einddatum, delta);
            StatistiekUitkomst uitkomst = stats.berekenStatistiekMACD();
            String sResult = uitkomst.print();
            String sKoop = "verkoopsignaal";
            if (isKoopsignaal)
                sKoop = "koopsignaal";
            addLogArea(sKoop + ": aantal gebeurtenissen = " + uitkomst.getAantalGebeurtenissen());
            addLogArea( " aantal dagen " + n + ":" + sResult);
            sResult = uitkomst.printDates();
            addLogArea(sResult);
        }

    }

    public void wijzigEinddatum() {
        System.out.println("wijzig einddatum");
        try {
            IDate einddatumNieuw = Util.toIDate(txtEinddatum.getText());
            this.einddatum = einddatumNieuw;
            lblEinddatum.setText(txtEinddatum.getText());
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
            GenereerStatistieken stats = new GenereerStatistieken(ticker, isKoopsignaal, 0, einddatum, delta);
            ArrayList<String> koopverkopen = stats.berekenBeleggenMACDStoploss();
            for (String msg: koopverkopen) {
                addLogArea(msg);
            }

        } catch (Exception e) {
            showMessage(e.getLocalizedMessage());
        }
    }

    public void toonLaatsteSignalen() {
        GenereerStatistieken gen = new GenereerStatistieken(
                ticker, true, 0, einddatum, 0);
        String message = gen.toonLaatsteSignalen();
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
