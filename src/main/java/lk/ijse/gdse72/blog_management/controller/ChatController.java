package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.service.ChatService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatbotService) {
        this.chatService = chatbotService;
    }

    @PostMapping
    public String chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        return chatService.getChatResponse(userMessage);
    }
}