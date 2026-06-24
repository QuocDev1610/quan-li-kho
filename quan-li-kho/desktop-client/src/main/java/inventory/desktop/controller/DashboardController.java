package inventory.desktop.controller;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.context.AppContext;
import inventory.desktop.http.ApiErrorParser;
import inventory.desktop.model.ApiPage;
import inventory.desktop.model.InvoiceDto;
import inventory.desktop.model.ProductDto;
import inventory.desktop.model.ProductInStockDto;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label monthlyReceiptsLabel;

    @FXML
    private Label monthlyIssuesLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private BarChart<String, Number> inventoryFlowChart;

    @FXML
    private TableView<LowStockItem> lowStockTable;

    @FXML
    private TableColumn<LowStockItem, String> productCodeColumn;

    @FXML
    private TableColumn<LowStockItem, String> productNameColumn;

    @FXML
    private TableColumn<LowStockItem, Number> quantityColumn;

    @FXML
    private TableColumn<LowStockItem, String> statusColumn;

    @FXML
    private void initialize() {
        setupLowStockTable();
        loadDashboardData();
    }

    private void loadDashboardData() {
        Task<DashboardData> task = new Task<>() {
            @Override
            protected DashboardData call() throws Exception {
                Type productPageType = TypeToken.getParameterized(ApiPage.class, ProductDto.class).getType();
                Type invoicePageType = TypeToken.getParameterized(ApiPage.class, InvoiceDto.class).getType();
                Type stockPageType = TypeToken.getParameterized(ApiPage.class, ProductInStockDto.class).getType();
                Map<String, String> query = Map.of("page", "1", "size", "1000");

                ApiPage<ProductDto> products = AppContext.apiClient().get("/api/products", query, productPageType);
                ApiPage<InvoiceDto> receipts = AppContext.apiClient().get("/api/goods-receipts", query, invoicePageType);
                ApiPage<InvoiceDto> issues = AppContext.apiClient().get("/api/goods-issues", query, invoicePageType);
                ApiPage<ProductInStockDto> stocks = AppContext.apiClient().get("/api/product-in-stocks", query, stockPageType);

                return new DashboardData(
                        products == null ? 0 : products.totalRows,
                        products == null || products.items == null ? List.of() : products.items,
                        receipts == null || receipts.items == null ? List.of() : receipts.items,
                        issues == null || issues.items == null ? List.of() : issues.items,
                        stocks == null || stocks.items == null ? List.of() : stocks.items
                );
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(() -> render(task.getValue())));
        task.setOnFailed(event -> Platform.runLater(() -> showError(task.getException())));

        Thread thread = new Thread(task, "dashboard-load-task");
        thread.setDaemon(true);
        thread.start();
    }

    private void render(DashboardData data) {
        List<ProductInStockDto> lowStockItems = data.lowStockItems();
        long totalProducts = data.totalProducts > 0 ? data.totalProducts : data.products.size();
        totalProductsLabel.setText(String.valueOf(totalProducts));
        monthlyReceiptsLabel.setText(String.valueOf(countCurrentMonth(data.receipts)));
        monthlyIssuesLabel.setText(String.valueOf(countCurrentMonth(data.issues)));
        lowStockLabel.setText(String.valueOf(lowStockItems.size()));
        setupInventoryChart(data.receipts, data.issues);
        lowStockTable.setItems(FXCollections.observableArrayList(toLowStockRows(lowStockItems)));
    }

    private void setupInventoryChart(List<InvoiceDto> receipts, List<InvoiceDto> issues) {
        XYChart.Series<String, Number> receiptSeries = new XYChart.Series<>();
        receiptSeries.setName("Nhập kho");
        XYChart.Series<String, Number> issueSeries = new XYChart.Series<>();
        issueSeries.setName("Xuất kho");

        for (int month = 1; month <= 12; month++) {
            String label = "T" + month;
            receiptSeries.getData().add(new XYChart.Data<>(label, sumQuantityByMonth(receipts, month)));
            issueSeries.getData().add(new XYChart.Data<>(label, sumQuantityByMonth(issues, month)));
        }

        inventoryFlowChart.getData().setAll(receiptSeries, issueSeries);
        inventoryFlowChart.setCategoryGap(18);
        inventoryFlowChart.setBarGap(5);
    }

    private void setupLowStockTable() {
        lowStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lowStockTable.setPlaceholder(new Label("Không có sản phẩm dưới định mức tồn kho."));
        productCodeColumn.setCellValueFactory(data -> data.getValue().productCodeProperty());
        productNameColumn.setCellValueFactory(data -> data.getValue().productNameProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        productCodeColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.22));
        productNameColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.38));
        quantityColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.14));
        statusColumn.prefWidthProperty().bind(lowStockTable.widthProperty().multiply(0.26));
        statusColumn.setCellFactory(column -> new TableCell<LowStockItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setText(null);
                Label badge = new Label(item);
                badge.getStyleClass().add("danger-badge");
                setGraphic(new StackPane(badge));
            }
        });
    }

    private int countCurrentMonth(List<InvoiceDto> invoices) {
        int currentMonth = LocalDate.now().getMonthValue();
        return (int) invoices.stream().filter(invoice -> monthOf(invoice.updateDate) == currentMonth).count();
    }

    private int sumQuantityByMonth(List<InvoiceDto> invoices, int month) {
        return invoices.stream()
                .filter(invoice -> monthOf(invoice.updateDate) == month)
                .map(invoice -> invoice.qty == null ? 0 : invoice.qty)
                .reduce(0, Integer::sum);
    }

    private int monthOf(String instantText) {
        if (instantText == null || instantText.isBlank()) {
            return -1;
        }
        try {
            return Instant.parse(instantText).atZone(ZoneId.systemDefault()).getMonthValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    private List<LowStockItem> toLowStockRows(List<ProductInStockDto> stocks) {
        List<LowStockItem> rows = new ArrayList<>();
        for (ProductInStockDto stock : stocks) {
            rows.add(new LowStockItem(
                    text(stock.productCode),
                    text(stock.productName),
                    stock.qty == null ? 0 : stock.qty,
                    (stock.qty != null && stock.qty <= 5) ? "Nguy cấp" : "Sắp hết"
            ));
        }
        return rows;
    }

    private void showError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Không tải được Dashboard");
        alert.setContentText(ApiErrorParser.friendlyException(throwable));
        alert.showAndWait();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static class DashboardData {
        private final List<ProductDto> products;
        private final List<InvoiceDto> receipts;
        private final List<InvoiceDto> issues;
        private final List<ProductInStockDto> stocks;
        private final long totalProducts;

        private DashboardData(long totalProducts, List<ProductDto> products, List<InvoiceDto> receipts, List<InvoiceDto> issues, List<ProductInStockDto> stocks) {
            this.totalProducts = totalProducts;
            this.products = products;
            this.receipts = receipts;
            this.issues = issues;
            this.stocks = stocks;
        }

        private List<ProductInStockDto> lowStockItems() {
            List<ProductInStockDto> result = new ArrayList<>();
            for (ProductInStockDto stock : stocks) {
                if (stock.qty != null && stock.qty <= 10) {
                    result.add(stock);
                }
            }
            return result;
        }
    }

    public static final class LowStockItem {
        private final SimpleStringProperty productCode;
        private final SimpleStringProperty productName;
        private final SimpleIntegerProperty quantity;
        private final SimpleStringProperty status;

        public LowStockItem(String productCode, String productName, int quantity, String status) {
            this.productCode = new SimpleStringProperty(productCode);
            this.productName = new SimpleStringProperty(productName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleStringProperty productCodeProperty() {
            return productCode;
        }

        public SimpleStringProperty productNameProperty() {
            return productName;
        }

        public SimpleIntegerProperty quantityProperty() {
            return quantity;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }
    }
}
