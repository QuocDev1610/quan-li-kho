package inventory.service;
import inventory.dao.entity.Invoice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.WebDataBinder;
@Service
public class GoodsReceiptReport{


        public void exportInvoicesToExcel(List<Invoice> invoiceList, HttpServletResponse response) throws IOException {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Danh Sách Nhập Kho");

                // --- 1. SETUP CÁC STYLE SIÊU ĐẸP ---

                // Style cho Header (Nền xanh đậm, chữ trắng, in đậm, căn giữa)
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                setBorders(headerStyle); // Hàm tự viết ở dưới để viền khung

                XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setFontHeight(12);
                headerStyle.setFont(headerFont);

                // Style cho Dữ liệu thường (Có viền)
                CellStyle dataStyle = workbook.createCellStyle();
                setBorders(dataStyle);

                // Style cho Dữ liệu căn giữa (Dành cho ID, Trạng thái)
                CellStyle centerDataStyle = workbook.createCellStyle();
                centerDataStyle.cloneStyleFrom(dataStyle);
                centerDataStyle.setAlignment(HorizontalAlignment.CENTER);

                // Style cho Tiền tệ (Ví dụ: 10,000,000)
                CellStyle currencyStyle = workbook.createCellStyle();
                currencyStyle.cloneStyleFrom(dataStyle);
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0")); // Dùng "#,##0.00" nếu có số thập phân

                // --- 2. TẠO HEADER ---
                Row headerRow = sheet.createRow(0);
                headerRow.setHeightInPoints(25); // Cho hàng tiêu đề cao hơn một chút nhìn cho thoáng

                String[] headers = {"ID", "Mã Phiếu", "Loại", "Giá","Số Lượng","Product" ,"Trạng Thái"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // --- 3. ĐỔ DỮ LIỆU ---
                int rowCount = 1;
                for (Invoice invoice : invoiceList) {
                    Row row = sheet.createRow(rowCount++);

                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(invoice.getId() != null ? invoice.getId() : 0);
                    cell0.setCellStyle(centerDataStyle); // Căn giữa ID

                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(invoice.getCode() != null ? invoice.getCode() : "");
                    cell1.setCellStyle(dataStyle);

                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(invoice.getType() != null ? invoice.getType().toString() : "");
                    cell2.setCellStyle(centerDataStyle);

                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(invoice.getQty() != null ? invoice.getQty() : 0);
                    cell3.setCellStyle(currencyStyle);

                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(invoice.getPrice() != null ? invoice.getPrice().doubleValue() : 0.0);
                    cell4.setCellStyle(centerDataStyle);

                    Cell cell5 = row.createCell(5);
                    cell5.setCellValue(invoice.getProduct().getName() != null ? invoice.getProduct().getName() : "");
                    cell5.setCellStyle(centerDataStyle);

                    Cell cell6 = row.createCell(4);
                    cell6.setCellValue(invoice.getActiveFlag() == 1 ? "Hoàn Thành" : "Đã hủy");
                    cell6.setCellStyle(centerDataStyle);
                }

                // --- 4. CĂN CHỈNH ĐỘ RỘNG CỘT TỰ ĐỘNG ---
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                    // Cộng thêm 1 chút khoảng trống để autoSize không bị quá sát viền
                    int currentWidth = sheet.getColumnWidth(i);
                    sheet.setColumnWidth(i, currentWidth + 1000);
                }

                // --- 5. XUẤT FILE ---
                ServletOutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
                outputStream.close();
            }
        }

        // Hàm tiện ích để set viền đen mỏng cho các ô (tránh lặp code)
        private void setBorders(CellStyle style) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        }
    }

