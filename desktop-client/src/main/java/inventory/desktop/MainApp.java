package inventory.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    private static final String LOGIN_VIEW = "/inventory/desktop/view/LoginView.fxml";
    private static final String GLOBAL_STYLE = "/inventory/desktop/styles/global-style.css";

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(resource(LOGIN_VIEW));
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(resource(GLOBAL_STYLE).toExternalForm());

        stage.setTitle("Inventory Login");
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    private URL resource(String path) {
        URL resource = MainApp.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Không tìm thấy resource: " + path);
        }
        return resource;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
