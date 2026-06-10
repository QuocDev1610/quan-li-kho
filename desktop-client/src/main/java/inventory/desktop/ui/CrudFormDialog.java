package inventory.desktop.ui;

import inventory.desktop.model.FormField;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrudFormDialog extends Dialog<Map<String, String>> {
    private final Map<String, Node> inputs = new LinkedHashMap<>();

    public CrudFormDialog(String title, List<FormField> fields, Map<String, String> initialValues) {
        setTitle(title);
        getDialogPane().getStyleClass().add("crud-dialog");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        getDialogPane().setContent(createForm(fields, initialValues));

        Node saveButton = getDialogPane().lookupButton(saveButtonType);
        saveButton.disableProperty().bind(javafx.beans.binding.Bindings.createBooleanBinding(
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

    private GridPane createForm(List<FormField> fields, Map<String, String> initialValues) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(18, 18, 10, 18));

        for (int i = 0; i < fields.size(); i++) {
            FormField field = fields.get(i);
            Label label = new Label(field.getLabel());
            label.getStyleClass().add("field-label");
            Node input = createInput(field, initialValues == null ? "" : initialValues.getOrDefault(field.getKey(), ""));
            inputs.put(field.getKey(), input);
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
            comboBox.getStyleClass().add("module-search");
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

        TextField input = new TextField(initialValue);
        input.setPromptText(field.getLabel());
        input.getStyleClass().add("module-search");
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
}
