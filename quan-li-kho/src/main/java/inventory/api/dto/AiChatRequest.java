package inventory.api.dto;

import javax.validation.constraints.NotBlank;

public class AiChatRequest {
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}