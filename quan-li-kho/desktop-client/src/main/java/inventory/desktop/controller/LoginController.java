package inventory.desktop.controller;

import inventory.desktop.context.AppContext;
import inventory.desktop.http.ApiErrorParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    private static final String MAIN_LAYOUT = "/inventory/desktop/view/MainLayout.fxml";
    private static final String GLOBAL_STYLE = "/inventory/desktop/styles/global-style.css";

    @FXML
    private TextField apiUrlField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        apiUrlField.setText("http://localhost:8080");
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String apiUrl = apiUrlField.getText() == null ? "" : apiUrlField.getText().trim();
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (apiUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ địa chỉ API, tài khoản và mật khẩu.");
            return;
        }

        errorLabel.setText("Đang đăng nhập...");
        AppContext.apiClient().setBaseUrl(apiUrl);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                AppContext.apiClient().login(username, password);
                return null;
            }
        };
        loginTask.setOnSucceeded(event -> Platform.runLater(() -> {
            try {
                openMainLayout();
            } catch (IOException ex) {
                errorLabel.setText("Không thể mở giao diện chính. Vui lòng kiểm tra lại tài nguyên FXML.");
            }
        }));
        loginTask.setOnFailed(event -> Platform.runLater(() -> errorLabel.setText(ApiErrorParser.friendlyException(loginTask.getException()))));
        Thread thread = new Thread(loginTask, "login-api-task");
        thread.setDaemon(true);
        thread.start();
    }

    private void openMainLayout() throws IOException {
        Parent root = FXMLLoader.load(resource(MAIN_LAYOUT));
        Scene scene = new Scene(root, 1280, 780);
        scene.getStylesheets().add(resource(GLOBAL_STYLE).toExternalForm());

        Stage stage = (Stage) apiUrlField.getScene().getWindow();
        stage.setTitle("Inventory Management System");
        stage.setMinWidth(1180);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private URL resource(String path) {
        URL resource = LoginController.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Không tìm thấy resource: " + path);
        }
        return resource;
    }
}
