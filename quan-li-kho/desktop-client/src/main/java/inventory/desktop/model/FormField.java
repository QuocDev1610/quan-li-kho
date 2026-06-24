package inventory.desktop.model;

import java.util.Collections;
import java.util.List;

public class FormField {
    private final String key;
    private final String label;
    private final boolean required;
    private final FieldType type;
    private final List<Option> options;

    public FormField(String key, String label, boolean required, FieldType type) {
        this(key, label, required, type, Collections.emptyList());
    }

    public FormField(String key, String label, boolean required, FieldType type, List<Option> options) {
        this.key = key;
        this.label = label;
        this.required = required;
        this.type = type;
        this.options = options == null ? Collections.emptyList() : options;
    }

    public static FormField text(String key, String label) {
        return new FormField(key, label, true, FieldType.TEXT);
    }

    public static FormField optionalText(String key, String label) {
        return new FormField(key, label, false, FieldType.TEXT);
    }

    public static FormField integer(String key, String label) {
        return new FormField(key, label, true, FieldType.INTEGER);
    }

    public static FormField decimal(String key, String label) {
        return new FormField(key, label, true, FieldType.DECIMAL);
    }

    public static FormField select(String key, String label, List<Option> options) {
        return new FormField(key, label, true, FieldType.SELECT, options);
    }

    public static Option option(Object value, String label) {
        return new Option(value == null ? "" : String.valueOf(value), label == null ? "" : label);
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRequired() {
        return required;
    }

    public FieldType getType() {
        return type;
    }

    public List<Option> getOptions() {
        return options;
    }

    public enum FieldType {
        TEXT,
        INTEGER,
        DECIMAL,
        SELECT
    }

    public static class Option {
        private final String value;
        private final String label;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
