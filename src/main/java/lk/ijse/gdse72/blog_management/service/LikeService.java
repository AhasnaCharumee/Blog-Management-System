package lk.ijse.gdse72.blog_management.service;

import lk.ijse.gdse72.blog_management.dto.LikeDTO;

public interface LikeService {
    LikeDTO toggleLike(Long postId, String userEmail);

    boolean hasUserLikedPost(Long postId, String userEmail);
}
