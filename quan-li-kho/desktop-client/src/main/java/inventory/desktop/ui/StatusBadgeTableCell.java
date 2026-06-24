package inventory.desktop.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;

public class StatusBadgeTableCell extends TableCell<ObservableList<String>, String> {
    @Override
    protected void updateItem(String value, boolean empty) {
        super.updateItem(value, empty);
        setText(null);

        if (empty || value == null || value.trim().isEmpty()) {
            setGraphic(null);
            return;
        }

        Label badge = new Label(displayText(value));
        badge.getStyleClass().addAll("status-badge", active(value) ? "status-badge-active" : "status-badge-inactive");

        StackPane wrapper = new StackPane(badge);
        wrapper.setAlignment(Pos.CENTER);
        setGraphic(wrapper);
    }

    private boolean active(String value) {
        String normalized = value.trim().toLowerCase();
        return "1".equals(normalized)
                || "true".equals(normalized)
                || "active".equals(normalized)
                || "hoạt động".equals(normalized)
                || "hoat dong".equals(normalized);
    }

    private String displayText(String value) {
        return active(value) ? "Hoạt động" : "Không hoạt động";
    }
}
