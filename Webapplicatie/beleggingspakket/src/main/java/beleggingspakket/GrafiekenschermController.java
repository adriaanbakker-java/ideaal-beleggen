package beleggingspakket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

//@Component
//@FxmlView("grafiekenscherm.fxml")
public class GrafiekenschermController implements Initializable {

    @FXML
    private Label weatherLabel;

    @Autowired
    public GrafiekenschermController() {
    }

    public void loadWeatherForecast(ActionEvent actionEvent) {
        WeatherService weatherService = new WeatherService();
        this.weatherLabel.setText(weatherService.getWeatherForecast());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
