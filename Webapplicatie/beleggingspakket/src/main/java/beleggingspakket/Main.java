package beleggingspakket;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main {

    private JavaFxApplication app;
    private MainController mainController;

    public Main(JavaFxApplication javaFxApplication) {
        this.app = javaFxApplication;
    }

    public void showMainStage() {
        app.showMainWindow();
    }

    public void toonGrafiekenscherm(String gekozenMarkt, String gekozenAandeel, int aantalKoersdagen, int aantalDagenRetro) {
        System.out.println("Vanuit Main: Toon grafiekenscherm");
        mainController.logInTextArea("Vanuit Main: Toon Grafiekenscherm");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
