package inventory.desktop.service;

import inventory.desktop.context.AppContext;
import inventory.desktop.model.ChatRequest;
import inventory.desktop.model.ChatResponse;

public class ChatApiService {
    public ChatResponse sendMessage(String message) throws Exception {
        return AppContext.apiClient().post(
                "/api/ai/chat",
                new ChatRequest(message),
                ChatResponse.class
        );
    }
}