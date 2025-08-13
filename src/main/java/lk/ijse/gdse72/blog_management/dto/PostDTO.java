package lk.ijse.gdse72.blog_management.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdDate;
    private String imagePath;
    private PostStatus status; // Add this line
    private int views;
    private int likes;
    private int commentsCount;
}