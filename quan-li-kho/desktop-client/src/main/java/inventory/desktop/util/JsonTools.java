package inventory.desktop.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonTools {
    private JsonTools() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    public static String readString(String json, String key) {
        String marker = "\"" + key + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        start += marker.length();
        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                value.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return value.toString();
            } else {
                value.append(c);
            }
        }
        return null;
    }

    public static List<Map<String, String>> readItems(String json) {
        String array = readArray(json, "items");
        if (array == null) {
            String dataObject = readObject(json, "data");
            if (dataObject != null) {
                List<Map<String, String>> one = new ArrayList<>();
                one.add(flattenObject(dataObject));
                return one;
            }
            return new ArrayList<>();
        }
        List<Map<String, String>> rows = new ArrayList<>();
        for (String object : splitObjects(array)) {
            rows.add(flattenObject(object));
        }
        return rows;
    }

    public static String buildJson(Map<String, String> values, Map<String, String> nestedFields) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            if (!first) {
                json.append(",");
            }
            first = false;
            if (nestedFields.containsKey(key)) {
                json.append("\"").append(nestedFields.get(key)).append("\":{\"id\":").append(value.trim()).append("}");
            } else if (looksNumeric(value)) {
                json.append("\"").append(key).append("\":").append(value.trim());
            } else {
                json.append("\"").append(key).append("\":\"").append(escape(value)).append("\"");
            }
        }
        json.append("}");
        return json.toString();
    }

    private static boolean looksNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }

    private static String readArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\":[");
        if (start < 0) {
            return null;
        }
        start = json.indexOf('[', start);
        int end = findMatching(json, start, '[', ']');
        return end < 0 ? null : json.substring(start + 1, end);
    }

    private static String readObject(String json, String key) {
        int start = json.indexOf("\"" + key + "\":{");
        if (start < 0) {
            return null;
        }
        start = json.indexOf('{', start);
        int end = findMatching(json, start, '{', '}');
        return end < 0 ? null : json.substring(start + 1, end);
    }

    private static int findMatching(String json, int start, char open, char close) {
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static List<String> splitObjects(String array) {
        List<String> objects = new ArrayList<>();
        int cursor = 0;
        while (cursor < array.length()) {
            int start = array.indexOf('{', cursor);
            if (start < 0) {
                break;
            }
            int end = findMatching(array, start, '{', '}');
            if (end < 0) {
                break;
            }
            objects.add(array.substring(start + 1, end));
            cursor = end + 1;
        }
        return objects;
    }

    private static Map<String, String> flattenObject(String object) {
        Map<String, String> map = new LinkedHashMap<>();
        int i = 0;
        while (i < object.length()) {
            int keyStart = object.indexOf('"', i);
            if (keyStart < 0) {
                break;
            }
            int keyEnd = object.indexOf('"', keyStart + 1);
            if (keyEnd < 0) {
                break;
            }
            String key = object.substring(keyStart + 1, keyEnd);
            int colon = object.indexOf(':', keyEnd);
            if (colon < 0) {
                break;
            }
            int valueStart = colon + 1;
            char first = object.charAt(valueStart);
            if (first == '"') {
                int valueEnd = findStringEnd(object, valueStart + 1);
                map.put(key, object.substring(valueStart + 1, valueEnd));
                i = valueEnd + 1;
            } else if (first == '{') {
                int end = findMatching(object, valueStart, '{', '}');
                Map<String, String> nested = flattenObject(object.substring(valueStart + 1, end));
                nested.forEach((nestedKey, nestedValue) -> map.put(key + "." + nestedKey, nestedValue));
                i = end + 1;
            } else if (first == '[') {
                int end = findMatching(object, valueStart, '[', ']');
                map.put(key, object.substring(valueStart, end + 1));
                i = end + 1;
            } else {
                int end = valueStart;
                while (end < object.length() && object.charAt(end) != ',') {
                    end++;
                }
                map.put(key, object.substring(valueStart, end).trim());
                i = end + 1;
            }
        }
        return map;
    }

    private static int findStringEnd(String object, int start) {
        boolean escaped = false;
        for (int i = start; i < object.length(); i++) {
            char c = object.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                return i;
            }
        }
        return object.length();
    }
}
