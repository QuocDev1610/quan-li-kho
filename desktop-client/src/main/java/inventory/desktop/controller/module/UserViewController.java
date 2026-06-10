package inventory.desktop.controller.module;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.context.AppContext;
import inventory.desktop.http.ApiErrorParser;
import inventory.desktop.model.FormField;
import inventory.desktop.model.RoleDto;
import inventory.desktop.model.UserDto;
import inventory.desktop.ui.StatusToggleButtonTableCell;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserViewController extends BaseModuleController<UserDto> {
    @FXML
    private void initialize() {
        configure(
                "Users",
                "Quản lý tài khoản nhân viên và vai trò sử dụng hệ thống.",
                List.of("ID", "Username", "Full Name", "Email", "Role", "Status"),
                true
        );
    }

    @Override
    protected String endpoint() {
        return "/api/users";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(UserDto.class);
    }

    @Override
    protected List<String> rowValues(UserDto item) {
        return List.of(text(item.id), text(item.userName), text(item.name), text(item.email), text(item.roleName), status(item.activeFlag));
    }

    @Override
    protected void configureColumn(TableColumn<ObservableList<String>, String> column, String columnName, int columnIndex) {
        if ("Status".equalsIgnoreCase(columnName)) {
            column.setCellFactory(value -> new StatusToggleButtonTableCell(this::toggleUserStatus));
        }
    }

    @Override
    protected Integer idOf(UserDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        if (editing) {
            return List.of(
                    FormField.text("userName", "Tài khoản"),
                    FormField.text("name", "Họ tên"),
                    FormField.text("email", "Email"),
                    FormField.integer("roleID", "ID vai trò")
            );
        }
        return List.of(
                FormField.text("userName", "Tài khoản"),
                FormField.text("password", "Mật khẩu"),
                FormField.text("name", "Họ tên"),
                FormField.text("email", "Email"),
                FormField.integer("roleID", "ID vai trò")
        );
    }

    @Override
    protected List<FormField> loadFormFields(boolean editing) throws Exception {
        Type optionsType = new TypeToken<Map<String, List<RoleDto>>>() {
        }.getType();
        Map<String, List<RoleDto>> options = AppContext.apiClient().get("/api/users/form-options", null, optionsType);
        List<FormField.Option> roles = new ArrayList<>();
        if (options != null && options.get("roles") != null) {
            for (RoleDto role : options.get("roles")) {
                roles.add(FormField.option(role.id, text(role.roleName) + " - " + text(role.description)));
            }
        }
        if (editing) {
            return List.of(
                    FormField.text("userName", "Tài khoản"),
                    FormField.text("name", "Họ tên"),
                    FormField.text("email", "Email"),
                    FormField.select("roleID", "Vai trò", roles)
            );
        }
        return List.of(
                FormField.text("userName", "Tài khoản"),
                FormField.text("password", "Mật khẩu"),
                FormField.text("name", "Họ tên"),
                FormField.text("email", "Email"),
                FormField.select("roleID", "Vai trò", roles)
        );
    }

    @Override
    protected Map<String, String> formValues(UserDto item) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("userName", text(item.userName));
        values.put("name", text(item.name));
        values.put("email", text(item.email));
        values.put("roleID", text(item.roleId));
        return values;
    }

    @Override
    protected Object requestBody(Map<String, String> values, UserDto editingItem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userName", stringValue(values, "userName"));
        if (editingItem == null) {
            body.put("password", stringValue(values, "password"));
        }
        body.put("name", stringValue(values, "name"));
        body.put("email", stringValue(values, "email"));
        body.put("roleID", integerValue(values, "roleID"));
        return body;
    }

    private void toggleUserStatus(int userId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                AppContext.apiClient().patch("/api/users/" + userId + "/status", null, UserDto.class);
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(this::refresh));
        task.setOnFailed(event -> Platform.runLater(() -> showToggleError(task.getException())));

        Thread thread = new Thread(task, "user-status-toggle-task");
        thread.setDaemon(true);
        thread.start();
    }

    private void showToggleError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Không thể đổi trạng thái tài khoản");
        alert.setContentText(ApiErrorParser.friendlyException(throwable));
        alert.showAndWait();
        refresh();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String status(Integer activeFlag) {
        return Integer.valueOf(1).equals(activeFlag) ? "Active" : "Inactive";
    }
}
