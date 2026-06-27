package inventory.desktop.controller.module;

import inventory.desktop.context.AppContext;
import inventory.desktop.http.ApiErrorParser;
import inventory.desktop.model.FormField;
import inventory.desktop.model.ProductInStockDto;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InStockViewController extends BaseModuleController<ProductInStockDto> {
    @FXML
    private Button reconcileButton;

    @FXML
    private void initialize() {
        configure(
                "In Stock",
                "Theo dõi tồn kho hiện tại và cảnh báo định mức.",
                List.of("ID", "Product Code", "Product Name", "Category", "Qty", "Price", "Stock Status"),
                false
        );
    }

    @Override
    protected String endpoint() {
        return "/api/product-in-stocks";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(ProductInStockDto.class);
    }

    @Override
    protected List<String> rowValues(ProductInStockDto item) {
        return List.of(text(item.id), text(item.productCode), text(item.productName), text(item.categoryName), text(item.qty), text(item.price), stockStatus(item.qty));
    }

    @Override
    protected void configureColumn(
            TableColumn<ObservableList<String>, String> column,
            String columnName,
            int columnIndex
    ) {
        if (!"Stock Status".equalsIgnoreCase(columnName)) {
            return;
        }
        column.getStyleClass().add("column-center");
        column.setCellFactory(ignored -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(status);
                badge.getStyleClass().addAll("stock-status-badge", stockStatusStyle(status));
                setText(null);
                setGraphic(new StackPane(badge));
            }
        });
    }

    @Override
    protected Integer idOf(ProductInStockDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        return Collections.emptyList();
    }

    @Override
    protected Map<String, String> formValues(ProductInStockDto item) {
        return Collections.emptyMap();
    }

    @Override
    protected Object requestBody(Map<String, String> values, ProductInStockDto editingItem) {
        return Collections.emptyMap();
    }

    @FXML
    private void reconcileStock() {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Hệ thống sẽ tính lại tồn kho từ toàn bộ phiếu nhập và phiếu xuất đang hoạt động.",
                ButtonType.YES,
                ButtonType.NO
        );
        confirm.setHeaderText("Đồng bộ lại số lượng tồn kho?");
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }

        reconcileButton.setDisable(true);
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                Integer updated = AppContext.apiClient().post(
                        "/api/product-in-stocks/reconcile",
                        Collections.emptyMap(),
                        Integer.class
                );
                return updated == null ? 0 : updated;
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(() -> {
            reconcileButton.setDisable(false);
            refresh();
            Alert result = new Alert(Alert.AlertType.INFORMATION);
            result.setHeaderText("Đồng bộ tồn kho thành công");
            result.setContentText("Đã cập nhật " + task.getValue() + " dòng tồn kho.");
            result.showAndWait();
        }));
        task.setOnFailed(event -> Platform.runLater(() -> {
            reconcileButton.setDisable(false);
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Không thể đồng bộ tồn kho");
            error.setContentText(ApiErrorParser.friendlyException(task.getException()));
            error.showAndWait();
        }));

        Thread thread = new Thread(task, "stock-reconcile-task");
        thread.setDaemon(true);
        thread.start();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String stockStatus(Integer qty) {
        if (qty == null) {
            return "Chưa xác định";
        }
        if (qty <= 0) {
            return "Hết hàng";
        }
        return qty <= 10 ? "Sắp hết" : "Ổn định";
    }

    private String stockStatusStyle(String status) {
        if ("Hết hàng".equals(status)) {
            return "stock-status-empty";
        }
        if ("Sắp hết".equals(status)) {
            return "stock-status-low";
        }
        return "stock-status-healthy";
    }
}
