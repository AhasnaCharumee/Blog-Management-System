package lk.ijse.gdse72.blog_management.dto;

import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdDate;
    private String imagePath;
    private PostStatus status; // Add this line
}