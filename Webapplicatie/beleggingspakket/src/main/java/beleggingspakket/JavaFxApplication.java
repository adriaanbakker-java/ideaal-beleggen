package beleggingspakket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(SpringBootExampleApplication.class)
                .run(args);
    }

    MainController mainController;
    Stage mainStage;
    Scene mainScene;
    Main main;

    public void showMainWindow() {
        mainStage.show();
    }



    @Override
    public void start(Stage stage) {
        mainStage = stage;
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainController.class);

        mainController = fxWeaver.getBean(MainController.class);
        mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        main = new Main(this);
        mainController.setMainObject(main);
        main.showMainStage();
//        stage.show();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    public void toonGrafiekenscherm(String gekozenMarkt,
                                    String gekozenAandeel,
                                    int aantalKoersdagen,
                                    int aantalDagenRetro) throws Exception {
       /* FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MyController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();*/

        Class<? extends JavaFxApplication> x = getClass();
        URL resourceURL = x.getResource("grafiekenscherm.fxml");
        FXMLLoader loader = new FXMLLoader(resourceURL);
        Parent grafiekenRoot = loader.load();
        GrafiekenschermController grafiekenschermController = loader.getController();
        grafiekenschermController.setMain(main);
        grafiekenschermController.setAandeel(gekozenAandeel);

        Scene grafiekenScene = new Scene(grafiekenRoot, 900, 600);

        Stage stage = new Stage();
        stage.setScene(grafiekenScene);
        stage.show();
    }


}
