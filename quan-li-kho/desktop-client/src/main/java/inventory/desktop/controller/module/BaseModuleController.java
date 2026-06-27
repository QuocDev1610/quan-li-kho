package inventory.desktop.controller.module;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.context.AppContext;
import inventory.desktop.http.ApiErrorParser;
import inventory.desktop.model.ApiPage;
import inventory.desktop.model.FormField;
import inventory.desktop.ui.CrudFormDialog;
import inventory.desktop.ui.StatusBadgeTableCell;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseModuleController<T> {
    @FXML
    protected Label moduleTitleLabel;

    @FXML
    protected Label moduleSubtitleLabel;

    @FXML
    protected TextField searchField;

    @FXML
    protected Button addButton;

    @FXML
    protected Button editButton;

    @FXML
    protected Button deleteButton;

    @FXML
    protected Button refreshButton;

    @FXML
    protected TableView<ObservableList<String>> tableView;

    private final ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
    private final ObservableList<T> currentItems = FXCollections.observableArrayList();
    private final Label tablePlaceholder = new Label("Chưa có dữ liệu.");
    private boolean writableModule;

    protected void configure(String title, String subtitle, List<String> columns, boolean writable) {
        this.writableModule = writable;
        moduleTitleLabel.setText(title);
        moduleSubtitleLabel.setText(subtitle);
        tablePlaceholder.getStyleClass().add("table-placeholder");
        tableView.setPlaceholder(tablePlaceholder);
        if (!tableView.getStyleClass().contains("data-table")) {
            tableView.getStyleClass().add("data-table");
        }
        tableView.setFixedCellSize(66);
        configureColumns(columns);
        configureSearch();
        if (addButton != null) {
            addButton.setDisable(!writable);
            addButton.setOnAction(event -> addItem());
        }
        if (editButton != null) {
            editButton.setDisable(!writable);
            editButton.setOnAction(event -> editItem());
        }
        if (deleteButton != null) {
            deleteButton.setDisable(!writable);
            deleteButton.setOnAction(event -> deleteItem());
        }
        if (refreshButton != null) {
            refreshButton.setOnAction(event -> refresh());
        }
        refresh();
    }

    @FXML
    protected void refresh() {
        setBusy(true);
        Task<List<T>> task = new Task<>() {
            @Override
            protected List<T> call() throws Exception {
                Type pageType = pageType();
                ApiPage<T> page = AppContext.apiClient().get(endpoint(), Map.of("page", "1", "size", "100"), pageType);
                return page == null || page.items == null ? Collections.emptyList() : page.items;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            currentItems.setAll(task.getValue());
            rows.setAll(currentItems.stream().map(item -> FXCollections.observableArrayList(rowValues(item))).collect(Collectors.toList()));
            setBusy(false);
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            setBusy(false);
            showError(task.getException());
        }));
        start(task, endpoint() + "-refresh");
    }

    protected abstract String endpoint();

    protected abstract Type pageType();

    protected abstract List<String> rowValues(T item);

    protected abstract Integer idOf(T item);

    protected abstract List<FormField> formFields(boolean editing);

    protected List<FormField> loadFormFields(boolean editing) throws Exception {
        return formFields(editing);
    }

    protected abstract Map<String, String> formValues(T item);

    protected abstract Object requestBody(Map<String, String> values, T editingItem);

    private void addItem() {
        openForm(null);
    }

    private void editItem() {
        T selected = selectedItem();
        if (selected == null) {
            info("Vui lòng chọn một dòng dữ liệu để sửa.");
            return;
        }
        openForm(selected);
    }

    private void openForm(T editingItem) {
        boolean editing = editingItem != null;
        setBusy(true);
        Task<FormPayload> task = new Task<>() {
            @Override
            protected FormPayload call() throws Exception {
                Map<String, String> values = editing ? formValues(editingItem) : new LinkedHashMap<>();
                return new FormPayload(loadFormFields(editing), values);
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            setBusy(false);
            FormPayload payload = task.getValue();
            CrudFormDialog dialog = new CrudFormDialog(editing ? "Cập nhật" : "Thêm mới", payload.fields, payload.values);
            Optional<Map<String, String>> result = dialog.showAndWait();
            result.ifPresent(values -> saveItem(editingItem, values));
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            setBusy(false);
            showError(task.getException());
        }));
        start(task, endpoint() + "-form-options");
    }

    private void deleteItem() {
        T selected = selectedItem();
        if (selected == null) {
            info("Vui lòng chọn một dòng dữ liệu để xóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Thao tác này sẽ cập nhật dữ liệu trong hệ thống. Bạn muốn tiếp tục?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận thao tác");
        confirm.setHeaderText("Xác nhận xóa bản ghi");
        styleAlert(confirm);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }
        setBusy(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                AppContext.apiClient().deleteForNoContent(endpoint() + "/" + idOf(selected));
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            setBusy(false);
            refresh();
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            setBusy(false);
            showError(task.getException());
        }));
        start(task, endpoint() + "-delete");
    }

    private void saveItem(T editingItem, Map<String, String> values) {
        setBusy(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Object request = requestBody(values, editingItem);
                if (editingItem == null) {
                    AppContext.apiClient().post(endpoint(), request, Object.class);
                } else {
                    AppContext.apiClient().put(endpoint() + "/" + idOf(editingItem), request, Object.class);
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> {
            setBusy(false);
            refresh();
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            setBusy(false);
            showError(task.getException());
        }));
        start(task, endpoint() + "-save");
    }

    private void configureColumns(List<String> columns) {
        tableView.getColumns().clear();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (int i = 0; i < columns.size(); i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columns.get(i));
            column.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(columnIndex)));
            column.setReorderable(false);
            column.setSortable(true);
            column.setMinWidth(minWidthFor(columns.get(i)));
            column.prefWidthProperty().bind(tableView.widthProperty().multiply(columnPercent(columns.get(i), columns.size())));
            configureColumnAlignment(column, columns.get(i));
            if (isActiveStatusColumn(columns.get(i))) {
                column.setCellFactory(value -> new StatusBadgeTableCell());
            }
            configureColumn(column, columns.get(i), columnIndex);
            tableView.getColumns().add(column);
        }
    }

    protected void configureColumn(TableColumn<ObservableList<String>, String> column, String columnName, int columnIndex) {
    }

    private void configureColumnAlignment(TableColumn<ObservableList<String>, String> column, String columnName) {
        if (isCenteredColumn(columnName)) {
            column.getStyleClass().add("column-center");
            return;
        }
        column.getStyleClass().add("column-left");
    }

    private void configureSearch() {
        FilteredList<ObservableList<String>> filteredRows = new FilteredList<>(rows, row -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();
            filteredRows.setPredicate(row -> keyword.isEmpty() || row.stream().anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(keyword)));
        });
        tableView.setItems(filteredRows);
    }

    private T selectedItem() {
        int rowIndex = tableView.getSelectionModel().getSelectedIndex();
        if (rowIndex < 0) {
            return null;
        }
        ObservableList<String> selectedRow = tableView.getItems().get(rowIndex);
        if (selectedRow == null || selectedRow.isEmpty()) {
            return null;
        }
        String id = selectedRow.get(0);
        return currentItems.stream().filter(item -> String.valueOf(idOf(item)).equals(id)).findFirst().orElse(null);
    }

    protected Integer integerValue(Map<String, String> values, String key) {
        return Integer.parseInt(values.get(key));
    }

    protected String stringValue(Map<String, String> values, String key) {
        return values.getOrDefault(key, "");
    }

    protected Type pageTypeFor(Class<T> itemType) {
        return TypeToken.getParameterized(ApiPage.class, itemType).getType();
    }

    private void setBusy(boolean busy) {
        if (refreshButton != null) {
            refreshButton.setDisable(busy);
        }
        if (addButton != null) {
            addButton.setDisable(busy || !writableModule);
        }
        if (editButton != null) {
            editButton.setDisable(busy || !writableModule);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(busy || !writableModule);
        }
        tablePlaceholder.setText(busy ? "Đang tải dữ liệu..." : "Chưa có dữ liệu phù hợp.");
    }

    private void start(Task<?> task, String name) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Không thể thực hiện thao tác");
        alert.setContentText(ApiErrorParser.friendlyException(throwable));
        styleAlert(alert);
        alert.showAndWait();
    }

    private void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Thông báo");
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private double columnPercent(String columnName, int columnCount) {
        String lower = columnName.toLowerCase(Locale.ROOT);
        if ("id".equals(lower)) {
            return 0.05;
        }
        if (lower.contains("image") || lower.contains("ảnh")) {
            return 0.15;
        }
        if (isActiveStatusColumn(columnName)) {
            return 0.15;
        }
        if (lower.contains("name") || lower.contains("sản phẩm") || lower.contains("product") || lower.contains("category") || lower.contains("danh mục")) {
            return 0.25;
        }
        return Math.max(0.10, 1.0 / Math.max(columnCount, 1));
    }

    private double minWidthFor(String columnName) {
        String lower = columnName.toLowerCase(Locale.ROOT);
        if ("id".equals(lower)) {
            return 72;
        }
        if (lower.contains("image") || lower.contains("ảnh")) {
            return 110;
        }
        if (isActiveStatusColumn(columnName)) {
            return 150;
        }
        if (lower.contains("name") || lower.contains("sản phẩm") || lower.contains("product") || lower.contains("category") || lower.contains("danh mục")) {
            return 190;
        }
        return 130;
    }

    private void styleAlert(Alert alert) {
        URL stylesheet = getClass().getResource("/inventory/desktop/styles/global-style.css");
        if (stylesheet != null) {
            alert.getDialogPane().getStylesheets().add(stylesheet.toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("modern-alert");
    }
    private boolean isCenteredColumn(String columnName) {
        String lower = columnName.toLowerCase(Locale.ROOT);
        return "id".equals(lower)
                || lower.contains("image")
                || lower.contains("ảnh")
                || isActiveStatusColumn(columnName);
    }

    private boolean isActiveStatusColumn(String columnName) {
        String lower = columnName.toLowerCase(Locale.ROOT).trim();
        return "status".equals(lower) || "trạng thái".equals(lower) || "trang thai".equals(lower);
    }

    private static class FormPayload {
        private final List<FormField> fields;
        private final Map<String, String> values;

        private FormPayload(List<FormField> fields, Map<String, String> values) {
            this.fields = fields;
            this.values = values;
        }
    }
}
