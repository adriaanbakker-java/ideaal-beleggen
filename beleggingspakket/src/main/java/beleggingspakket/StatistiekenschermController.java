package beleggingspakket;

import beleggingspakket.util.IDate;
import beleggingspakket.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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


    private String aandeelnaam;
    private IDate einddatum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize Statistiekenscherm ");
    }

    public void setAandeelnaam(String aandeelnaam) {
        this.aandeelnaam = aandeelnaam;
        lblHeading.setText("Statistieken voor aandeel " + aandeelnaam);
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
            showMessage("genereren stats " + isKoopsignaal + "|" + delta + "|" + nDagen );
        } catch (Exception e) {
            showMessage(e.getLocalizedMessage());
        }
    }

    private void showMessage(String localizedMessage) {
        txtMessage.setText(localizedMessage);
    }
}
