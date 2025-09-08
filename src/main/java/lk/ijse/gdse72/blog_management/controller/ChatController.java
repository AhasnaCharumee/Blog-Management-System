package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.ChatMessageDTO;
import lk.ijse.gdse72.blog_management.entity.ChatMessageEntity;
import lk.ijse.gdse72.blog_management.repository.ChatMessageRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final Map<String, String> profileCache = new ConcurrentHashMap<>(); // Cache for profile URLs

    public ChatController(ChatMessageRepository chatMessageRepository,
                          UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO broadcastMessage(ChatMessageDTO message) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setSender(message.getSender());
        entity.setContent(message.getContent());
        entity.setImageUrl(message.getImageUrl());
        entity.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(entity);

        String profileUrl = getProfileUrl(entity.getSender());

        return new ChatMessageDTO(
                entity.getSender(),
                entity.getContent(),
                entity.getImageUrl(),
                profileUrl,
                entity.getTimestamp()
        );
    }

    @GetMapping("/messages")
    public List<ChatMessageDTO> getAllMessages() {
        return chatMessageRepository.findAll().stream()
                .map(e -> new ChatMessageDTO(
                        e.getSender(),
                        e.getContent(),
                        e.getImageUrl(),
                        getProfileUrl(e.getSender()),
                        e.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    private String getProfileUrl(String senderEmail) {
        return profileCache.computeIfAbsent(senderEmail, email ->
                userRepository.findByEmail(email)
                        .map(user -> {
                            if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                                return "http://localhost:8080/api/users/images/" + user.getProfileImagePath();
                            }
                            // Local default instead of placeholder.com
                            return "http://localhost:8080/api/users/images/default.png";
                        })
                        .orElse("http://localhost:8080/api/users/images/default.png")
        );
    }

}