package inventory.desktop.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.StackPane;

import java.util.function.IntConsumer;

public class StatusToggleButtonTableCell extends TableCell<ObservableList<String>, String> {
    private final IntConsumer onToggle;

    public StatusToggleButtonTableCell(IntConsumer onToggle) {
        this.onToggle = onToggle;
    }

    @Override
    protected void updateItem(String value, boolean empty) {
        super.updateItem(value, empty);
        setText(null);

        if (empty || value == null || value.trim().isEmpty()) {
            setGraphic(null);
            return;
        }

        Button button = new Button(active(value) ? "Hoạt động" : "Không hoạt động");
        button.getStyleClass().addAll("status-toggle-button", active(value) ? "status-toggle-active" : "status-toggle-inactive");
        button.setOnAction(event -> {
            ObservableList<String> row = getTableRow() == null ? null : getTableRow().getItem();
            if (row == null || row.isEmpty()) {
                return;
            }
            try {
                button.setDisable(true);
                onToggle.accept(Integer.parseInt(row.get(0)));
            } catch (NumberFormatException ignored) {
                button.setDisable(false);
            }
        });

        StackPane wrapper = new StackPane(button);
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
}
