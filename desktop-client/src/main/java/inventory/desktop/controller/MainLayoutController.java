package inventory.desktop.controller;

import inventory.desktop.context.AppContext;
import inventory.desktop.navigation.ViewNavigator;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
public class MainLayoutController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Label accountLabel;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label pageSubtitleLabel;

    @FXML
    private TextField globalSearchField;

    @FXML
    private StackPane notificationButton;

    @FXML
    private HBox profileArea;

    @FXML
    private Label profileNameLabel;

    @FXML
    private Label avatarInitialLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button goodsReceiptsButton;

    @FXML
    private Button goodsIssuesButton;

    @FXML
    private Button inStockButton;

    @FXML
    private Button usersButton;

    private ContextMenu profileMenu;

    @FXML
    private void initialize() {
        String userName = AppContext.sessionManager().getUserName().orElse("admin@inventory");
        accountLabel.setText(userName);
        profileNameLabel.setText(displayName(userName));
        avatarInitialLabel.setText(initialOf(userName));
        configureProfileMenu();
        ViewNavigator.initialize(contentArea);
        openDashboard();
    }

    @FXML
    private void openDashboard() {
        activate(dashboardButton);
        setPageTitle("Dashboard", "Tổng quan hoạt động kho hàng");
        ViewNavigator.navigateTo(ViewNavigator.DASHBOARD_VIEW);
    }

    @FXML
    private void openCategories() {
        activate(categoriesButton);
        setPageTitle("Danh mục", "Quản lý nhóm hàng và phân loại sản phẩm");
        ViewNavigator.navigateTo(ViewNavigator.CATEGORY_VIEW);
    }

    @FXML
    private void openProducts() {
        activate(productsButton);
        setPageTitle("Sản phẩm", "Quản lý danh sách hàng hóa");
        ViewNavigator.navigateTo(ViewNavigator.PRODUCT_VIEW);
    }

    @FXML
    private void openGoodsReceipts() {
        activate(goodsReceiptsButton);
        setPageTitle("Nhập kho", "Tạo phiếu nhập và theo dõi hàng vào kho");
        ViewNavigator.navigateTo(ViewNavigator.GOODS_RECEIPT_VIEW);
    }

    @FXML
    private void openGoodsIssues() {
        activate(goodsIssuesButton);
        setPageTitle("Xuất kho", "Kiểm soát phiếu xuất và hàng rời kho");
        ViewNavigator.navigateTo(ViewNavigator.GOODS_ISSUE_VIEW);
    }

    @FXML
    private void openInStock() {
        activate(inStockButton);
        setPageTitle("Tồn kho", "Theo dõi số lượng tồn hiện tại");
        ViewNavigator.navigateTo(ViewNavigator.IN_STOCK_VIEW);
    }

    @FXML
    private void openUsers() {
        activate(usersButton);
        setPageTitle("Người dùng", "Quản lý tài khoản và phân quyền");
        ViewNavigator.navigateTo(ViewNavigator.USER_VIEW);
    }

    @FXML
    private void showNotifications() {
        ContextMenu menu = new ContextMenu();
        MenuItem emptyItem = new MenuItem("Chưa có thông báo mới");
        emptyItem.setDisable(true);
        menu.getItems().add(emptyItem);
        menu.show(notificationButton, Side.BOTTOM, -140, 8);
    }

    @FXML
    private void showProfileMenu() {
        profileMenu.show(profileArea, Side.BOTTOM, 0, 8);
    }

    public void setPageTitle(String title, String subtitle) {
        pageTitleLabel.setText(title == null || title.isBlank() ? "Inventory" : title);
        pageSubtitleLabel.setText(subtitle == null || subtitle.isBlank() ? "" : subtitle);
    }

    private void configureProfileMenu() {
        profileMenu = new ContextMenu();
        profileMenu.getStyleClass().add("profile-context-menu");

        MenuItem accountItem = new MenuItem("Tài khoản của tôi");
        MenuItem settingsItem = new MenuItem("Cài đặt hệ thống");
        MenuItem logoutItem = new MenuItem("Đăng xuất");
        logoutItem.getStyleClass().add("logout-menu-item");
        logoutItem.setOnAction(event -> logout());

        profileMenu.getItems().addAll(accountItem, settingsItem, new SeparatorMenuItem(), logoutItem);
    }

    private void logout() {
        AppContext.apiClient().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventory/desktop/view/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/inventory/desktop/styles/global-style.css").toExternalForm());
            Stage stage = (Stage) profileArea.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inventory Login");
            stage.centerOnScreen();
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể quay lại màn hình đăng nhập.", ex);
        }
    }

    private String displayName(String userName) {
        if (userName == null || userName.isBlank()) {
            return "Lê Ngọc Quốc";
        }
        return userName;
    }

    private String initialOf(String text) {
        String display = displayName(text).trim();
        return display.isEmpty() ? "A" : display.substring(0, 1).toUpperCase();
    }

    private void activate(Button activeButton) {
        Button[] buttons = {
                dashboardButton,
                categoriesButton,
                productsButton,
                goodsReceiptsButton,
                goodsIssuesButton,
                inStockButton,
                usersButton
        };
        for (Button button : buttons) {
            button.getStyleClass().remove("sidebar-button-active");
        }
        if (!activeButton.getStyleClass().contains("sidebar-button-active")) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }
}
