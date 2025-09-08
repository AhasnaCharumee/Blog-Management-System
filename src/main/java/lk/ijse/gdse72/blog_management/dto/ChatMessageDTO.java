package lk.ijse.gdse72.blog_management.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private String sender;
    private String content;
    private String imageUrl;
    private String senderProfile; // profile image URL
    private LocalDateTime timestamp;

    public ChatMessageDTO() {}

    public ChatMessageDTO(String sender, String content, String imageUrl, String senderProfile, LocalDateTime timestamp) {
        this.sender = sender;
        this.content = content;
        this.imageUrl = imageUrl;
        this.senderProfile = senderProfile;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(String senderProfile) {
        this.senderProfile = senderProfile;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
