package inventory.desktop.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public final class ViewNavigator {
    public static final String DASHBOARD_VIEW = "/inventory/desktop/view/DashboardView.fxml";
    public static final String CATEGORY_VIEW = "/inventory/desktop/view/CategoryView.fxml";
    public static final String PRODUCT_VIEW = "/inventory/desktop/view/ProductView.fxml";
    public static final String GOODS_RECEIPT_VIEW = "/inventory/desktop/view/GoodsReceiptView.fxml";
    public static final String GOODS_ISSUE_VIEW = "/inventory/desktop/view/GoodsIssueView.fxml";
    public static final String IN_STOCK_VIEW = "/inventory/desktop/view/InStockView.fxml";
    public static final String USER_VIEW = "/inventory/desktop/view/UserView.fxml";
    public static final String CHAT_VIEW = "/inventory/desktop/view/ChatView.fxml";
    private static StackPane contentArea;

    private ViewNavigator() {
    }

    public static void initialize(StackPane targetContentArea) {
        contentArea = targetContentArea;
    }

    public static void navigateTo(String fxmlPath) {
        ensureInitialized();
        contentArea.getChildren().setAll(load(fxmlPath));
    }

    private static Node load(String fxmlPath) {
        URL resource = ViewNavigator.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("Không tìm thấy màn hình: " + fxmlPath);
        }
        try {
            return FXMLLoader.load(resource);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể tải màn hình: " + fxmlPath, ex);
        }
    }

    private static void ensureInitialized() {
        if (contentArea == null) {
            throw new IllegalStateException("ViewNavigator chưa được initialize với contentArea.");
        }
    }
}
