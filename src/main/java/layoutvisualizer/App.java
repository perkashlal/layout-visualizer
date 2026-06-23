package layoutvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view.fxml"));
        Parent root = fxmlLoader.load();
        Controller vc = fxmlLoader.getController();

        Slider slider = vc.getSlider();
        slider.setMin(1);
        slider.setSnapToTicks(true);
        slider.setValue(10.0);
        slider.setMax(10.0);

        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1.0);

        ScrollPane scroller = vc.getScroller();
        scroller.setFitToWidth(true);
        scroller.setFitToHeight(true);

        scene = new Scene(root, 1500, 640);
        stage.setTitle("Layout Visualizer");
        stage.setScene(scene);

        stage.show();

        stage.setOnCloseRequest(event -> {
            try{
                vc.closeAll();
            }catch(Exception ex){
                System.out.println(ex);
            }
            javafx.application.Platform.exit();   
            System.exit(0);
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }



}