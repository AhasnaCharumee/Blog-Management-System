package lk.ijse.gdse72.blog_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    private Long id;
    private Long userId;
    private Long postId;
}