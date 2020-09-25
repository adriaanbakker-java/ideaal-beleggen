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
    private WeatherService weatherService;

    @FXML
    private Label weatherLabel;

    @FXML
    private TextField txtKoersfolder;

    @Autowired
    public MainController(WeatherService weatherService) {
        this.weatherService = weatherService;
        pricefolder = Constants.getPricefolder();
    }

    public void loadWeatherForecast(ActionEvent actionEvent) {
        this.weatherLabel.setText(weatherService.getWeatherForecast());
        //initialize();
    }




    public void logInTextArea(String infostring) {
    }

    public void initialize() {
        pricefolder = Constants.getPricefolder();
        txtKoersfolder.setText(pricefolder);
    }
}
