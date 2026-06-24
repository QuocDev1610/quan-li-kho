package inventory.desktop.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class TableExcelExporter {
    private TableExcelExporter() {
    }

    public static void exportToExcel(TableView<ObservableList<String>> tableView, Window owner, String defaultName) {
        if (tableView.getItems() == null || tableView.getItems().isEmpty()) {
            showInfo("Không có dữ liệu để xuất Excel.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn nơi lưu file Excel");
        chooser.setInitialFileName(defaultName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));
        File target = chooser.showSaveDialog(owner);
        if (target == null) {
            return;
        }

        List<String> headers = snapshotHeaders(tableView);
        List<List<String>> rows = snapshotRows(tableView);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                writeWorkbook(target, headers, rows);
                return null;
            }
        };
        task.setOnSucceeded(event -> Platform.runLater(() -> showInfo("Xuất Excel thành công:\n" + target.getAbsolutePath())));
        task.setOnFailed(event -> Platform.runLater(() -> showError("Không thể xuất Excel", task.getException())));

        Thread thread = new Thread(task, "table-excel-export-task");
        thread.setDaemon(true);
        thread.start();
    }

    private static List<String> snapshotHeaders(TableView<ObservableList<String>> tableView) {
        List<String> headers = new ArrayList<>();
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            headers.add(column.getText());
        }
        return headers;
    }

    private static List<List<String>> snapshotRows(TableView<ObservableList<String>> tableView) {
        List<List<String>> rows = new ArrayList<>();
        for (ObservableList<String> item : tableView.getItems()) {
            rows.add(new ArrayList<>(item));
        }
        return rows;
    }

    private static void writeWorkbook(File target, List<String> headers, List<List<String>> rows) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(target)) {
            Sheet sheet = workbook.createSheet("Data");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                Cell cell = headerRow.createCell(columnIndex);
                cell.setCellValue(headers.get(columnIndex));
                cell.setCellStyle(headerStyle);
            }

            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                List<String> values = rows.get(rowIndex);
                for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                    String value = columnIndex < values.size() ? values.get(columnIndex) : "";
                    row.createCell(columnIndex).setCellValue(value);
                }
            }

            for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
            }
            workbook.write(outputStream);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Xuất Excel");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showError(String header, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(throwable == null ? "Đã xảy ra lỗi không xác định." : throwable.getMessage());
        alert.showAndWait();
    }
}
