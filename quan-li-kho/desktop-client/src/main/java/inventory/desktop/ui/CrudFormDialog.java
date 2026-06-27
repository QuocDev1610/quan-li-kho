package inventory.desktop.ui;

import inventory.desktop.model.FormField;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrudFormDialog extends Dialog<Map<String, String>> {
    private final Map<String, Node> inputs = new LinkedHashMap<>();

    public CrudFormDialog(String title, List<FormField> fields, Map<String, String> initialValues) {
        DialogPane pane = getDialogPane();
        setTitle(title);
        pane.setHeader(null);
        pane.getStyleClass().add("crud-dialog");
        pane.setPrefWidth(620);
        pane.setMinWidth(560);
        attachGlobalStylesheet(pane);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        pane.setContent(createContent(title, fields, initialValues));

        Button saveButton = (Button) pane.lookupButton(saveButtonType);
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);
        saveButton.getStyleClass().addAll("button-primary", "crud-save-button");
        cancelButton.setText("Hủy");
        cancelButton.getStyleClass().addAll("button-secondary", "crud-cancel-button");

        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> fields.stream().anyMatch(field -> field.isRequired() && readValue(inputs.get(field.getKey())).trim().isEmpty()),
                observableDependencies(inputs.values())
        ));

        setResultConverter(buttonType -> {
            if (buttonType != saveButtonType) {
                return null;
            }
            Map<String, String> values = new LinkedHashMap<>();
            inputs.forEach((key, field) -> values.put(key, readValue(field).trim()));
            return values;
        });
    }

    private VBox createContent(String title, List<FormField> fields, Map<String, String> initialValues) {
        VBox content = new VBox(22);
        content.getStyleClass().add("crud-dialog-content");

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("crud-dialog-header");

        FontIcon icon = new FontIcon(title.toLowerCase().contains("cập") ? "fas-pen" : "fas-plus");
        icon.setIconSize(16);
        icon.getStyleClass().add("crud-dialog-icon");

        VBox titles = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("crud-dialog-title");
        Label subtitleLabel = new Label("Nhập thông tin bắt buộc, sau đó bấm Lưu để cập nhật dữ liệu.");
        subtitleLabel.getStyleClass().add("crud-dialog-subtitle");
        titles.getChildren().addAll(titleLabel, subtitleLabel);
        HBox.setHgrow(titles, Priority.ALWAYS);

        header.getChildren().addAll(icon, titles);
        content.getChildren().addAll(header, createForm(fields, initialValues));
        return content;
    }

    private GridPane createForm(List<FormField> fields, Map<String, String> initialValues) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("crud-dialog-grid");
        grid.setHgap(18);
        grid.setVgap(14);
        grid.setPadding(new Insets(0));

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(138);
        labelColumn.setPrefWidth(150);

        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHgrow(Priority.ALWAYS);
        inputColumn.setFillWidth(true);
        grid.getColumnConstraints().addAll(labelColumn, inputColumn);

        for (int i = 0; i < fields.size(); i++) {
            FormField field = fields.get(i);
            Label label = new Label(field.getLabel() + (field.isRequired() ? " *" : ""));
            label.getStyleClass().add("crud-field-label");

            Node input = createInput(field, initialValues == null ? "" : initialValues.getOrDefault(field.getKey(), ""));
            inputs.put(field.getKey(), input);
            GridPane.setHgrow(input, Priority.ALWAYS);

            grid.add(label, 0, i);
            grid.add(input, 1, i);
        }
        return grid;
    }

    private Node createInput(FormField field, String initialValue) {
        if (field.getType() == FormField.FieldType.SELECT) {
            ComboBox<FormField.Option> comboBox = new ComboBox<>();
            comboBox.getItems().setAll(field.getOptions());
            comboBox.setMaxWidth(Double.MAX_VALUE);
            comboBox.setPromptText(field.getLabel());
            comboBox.getStyleClass().add("crud-input");
            comboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(FormField.Option option) {
                    return option == null ? "" : option.getLabel();
                }

                @Override
                public FormField.Option fromString(String value) {
                    return null;
                }
            });
            field.getOptions().stream()
                    .filter(option -> option.getValue().equals(initialValue))
                    .findFirst()
                    .ifPresent(comboBox::setValue);
            if (field.isRequired() && comboBox.getValue() == null && !comboBox.getItems().isEmpty()) {
                comboBox.setValue(comboBox.getItems().get(0));
            }
            return comboBox;
        }

        TextInputControl input = "password".equalsIgnoreCase(field.getKey()) ? new PasswordField() : new TextField();
        input.setText(initialValue);
        input.setPromptText(field.getLabel());
        input.getStyleClass().add("crud-input");
        return input;
    }

    private String readValue(Node input) {
        if (input instanceof TextInputControl) {
            TextInputControl textInput = (TextInputControl) input;
            return textInput.getText() == null ? "" : textInput.getText();
        }
        if (input instanceof ComboBox<?>) {
            ComboBox<?> comboBox = (ComboBox<?>) input;
            Object value = comboBox.getValue();
            if (value instanceof FormField.Option) {
                FormField.Option option = (FormField.Option) value;
                return option.getValue();
            }
        }
        return "";
    }

    private Observable[] observableDependencies(Iterable<Node> nodes) {
        List<Observable> observables = new ArrayList<>();
        for (Node node : nodes) {
            if (node instanceof TextInputControl) {
                TextInputControl textInput = (TextInputControl) node;
                observables.add(textInput.textProperty());
            } else if (node instanceof ComboBox<?>) {
                ComboBox<?> comboBox = (ComboBox<?>) node;
                observables.add(comboBox.valueProperty());
            }
        }
        return observables.toArray(Observable[]::new);
    }

    private void attachGlobalStylesheet(DialogPane pane) {
        URL stylesheet = getClass().getResource("/inventory/desktop/styles/global-style.css");
        if (stylesheet != null) {
            pane.getStylesheets().add(stylesheet.toExternalForm());
        }
    }
}
