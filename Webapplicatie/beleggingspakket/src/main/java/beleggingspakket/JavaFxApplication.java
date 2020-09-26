package beleggingspakket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

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
    Main main;

    public void showMainWindow() {
        mainStage.show();
    }



    @Override
    public void start(Stage stage) {
        mainStage = stage;
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainController.class);


        /*
        HOE KRIJGEN WE NU HET CONTROLLER OBJECT VAN HET SCHERM??
        HET VOLGENDE DOET HET NIET - ander object kennelijk dan het werkelijke controller object
         mainController = fxWeaver.loadController(MainController.class);
         mainController.setMainObject(main);*/

        mainController = fxWeaver.getBean(MainController.class);
        Scene scene = new Scene(root);
        mainStage.setScene(scene);

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

}
