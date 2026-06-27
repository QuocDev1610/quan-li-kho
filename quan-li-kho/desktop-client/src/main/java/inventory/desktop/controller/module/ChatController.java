package inventory.desktop.controller.module;

import inventory.desktop.model.ChatResponse;
import inventory.desktop.service.ChatApiService;
import inventory.desktop.http.ApiErrorParser;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatController {

    // --- CÁC THÀNH PHẦN GIAO DIỆN (Nối với ChatView.fxml) ---
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextArea messageInput;

    @FXML
    private Button sendButton;

    // --- DỊCH VỤ GỌI API ---
    private final ChatApiService chatApiService = new ChatApiService();

    // --- HÀM KHỞI TẠO (Chạy ngay khi mở màn hình Chat) ---
    @FXML
    public void initialize() {
        // Gửi một tin nhắn chào mừng mặc định từ AI
        addAiMessage("Xin chào! Tôi là trợ lý AI của hệ thống Quản lý kho. Tôi có thể giúp gì cho bạn hôm nay?");

        // Cho phép ấn phím Enter để gửi tin nhắn (thay vì phải dùng chuột bấm nút)
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume(); // Ngăn không cho Enter tạo dòng mới
                sendMessage();
            }
        });
    }

    // --- LUỒNG XỬ LÝ CHÍNH (Đoạn code tuyệt vời của bạn) ---
    @FXML
    private void sendMessage() {
        String message = messageInput.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        // 1. Hiển thị tin nhắn của người dùng lên màn hình
        addUserMessage(message);
        messageInput.clear();

        // Cấm nút gửi và ô nhập trong lúc chờ AI trả lời để tránh bấm liên tục
        setLoadingState(true);

        // 2. Tạo Task chạy ngầm để gọi API (Tránh đơ giao diện)
        Task<ChatResponse> task = new Task<>() {
            @Override
            protected ChatResponse call() throws Exception {
                return chatApiService.sendMessage(message);
            }
        };

        // 3. Xử lý khi AI trả lời thành công
        task.setOnSucceeded(event -> {
            ChatResponse response = task.getValue();
            addAiMessage(response.getAnswer()); // Lưu ý: Dùng getAnswer() tùy thuộc vào class ChatResponse của bạn
            setLoadingState(false);
        });

        // 4. Xử lý khi mạng lỗi hoặc API lỗi
        task.setOnFailed(event -> {
            addAiMessage(ApiErrorParser.friendlyException(task.getException()));
            setLoadingState(false);
        });

        // Kích hoạt luồng chạy ngầm
        Thread thread = new Thread(task, "ai-chat-task");
        thread.setDaemon(true);
        thread.start();
    }

    // --- CÁC HÀM TIỆN ÍCH VẼ GIAO DIỆN ---

    private void addUserMessage(String text) {
        Label messageLabel = createBubble(text, "-fx-background-color: #0078D7; -fx-text-fill: white;");
        HBox bubbleWrapper = new HBox(messageLabel);
        bubbleWrapper.setAlignment(Pos.CENTER_RIGHT); // Căn phải cho user

        messageContainer.getChildren().add(bubbleWrapper);
        scrollToBottom();
    }

    private void addAiMessage(String text) {
        Label messageLabel = createBubble(text, "-fx-background-color: #E5E5EA; -fx-text-fill: black;");
        HBox bubbleWrapper = new HBox(messageLabel);
        bubbleWrapper.setAlignment(Pos.CENTER_LEFT); // Căn trái cho AI

        messageContainer.getChildren().add(bubbleWrapper);
        scrollToBottom();
    }

    // Hàm tạo "bong bóng" chat bo góc
    private Label createBubble(String text, String extraStyle) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(400); // Giới hạn chiều rộng để text tự động xuống dòng
        label.setStyle(extraStyle + " -fx-padding: 10px 15px; -fx-background-radius: 15px; -fx-font-size: 14px;");
        return label;
    }

    // Khóa/Mở khóa ô nhập liệu
    private void setLoadingState(boolean isLoading) {
        messageInput.setDisable(isLoading);
        sendButton.setDisable(isLoading);
        if (isLoading) {
            messageInput.setPromptText("AI đang suy nghĩ...");
        } else {
            messageInput.setPromptText("Hỏi trợ lý AI về cách nhập, xuất kho...");
            messageInput.requestFocus(); // Tự động trỏ chuột lại vào ô nhập
        }
    }

    // Tự động cuộn thanh cuộn xuống dưới cùng khi có tin nhắn mới
    private void scrollToBottom() {
        // Cần một chút độ trễ để giao diện kịp tính toán lại chiều cao
        javafx.application.Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}
