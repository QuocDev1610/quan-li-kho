package inventory.controller;

import inventory.api.dto.AiChatRequest;
import inventory.api.dto.AiChatResponse;
import inventory.service.AiChatException;
import inventory.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@Valid @RequestBody AiChatRequest request) {
        try {
            String answer = aiChatService.ask(request.getMessage());
            return ResponseEntity.ok(new AiChatResponse(answer));
        } catch (AiChatException ex) {
            return ResponseEntity.status(ex.getHttpStatus()).body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }
}
