package beleggingspakket;

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
    private TextField txtNDagen;
    @FXML
    private TextArea taLogArea;

    private String ticker;
    private IDate einddatum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize Statistiekenscherm ");
    }

    public void setTicker(String aTicker) {
        this.ticker = aTicker;
        lblHeading.setText("Statistieken voor aandeel " + aTicker);
    }

    public void setEinddatum(IDate einddatum) {
        this.einddatum = einddatum;
        lblEinddatum.setText("Einddatum:" + einddatum.toString());
    }

    public void genereer() {
        try {
            boolean isKoopsignaal = chkKoopsignaal.isSelected();
            double delta = Util.toDouble(txtDelta.getText());
            int nDagen = Integer.parseInt(txtNDagen.getText());
            String sMsg = "genereren stats " + isKoopsignaal + "|" + delta + "|" + nDagen;
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
            }


        } catch (Exception e) {
            showMessage(e.getLocalizedMessage());
        }
    }

    private void addLogArea(String sMsg) {
        taLogArea.setText(taLogArea.getText() + "\n" + sMsg);
    }

    private void showMessage(String localizedMessage) {
        txtMessage.setText(localizedMessage);
    }
}
