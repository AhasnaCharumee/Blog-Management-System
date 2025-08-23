package lk.ijse.gdse72.blog_management.service;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, MultipartFile image, User user);

    Map<String, Object> getUserPostInteractionStats(User user);

    List<PostDTO> getAllPosts();

    PostDTO getPostById(Long id);

    PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image);

    void deletePost(Long id);

    PostDTO approvePost(Long id);

    PostDTO rejectPost(Long id);

    // In PostService.java
    List<PostDTO> getAllPostsForAdmin();

    // src/main/java/lk/ijse/gdse72/blog_management/service/PostService.java
    void updatePost(Long postId, PostDTO postDTO, Long authenticatedUserId);
    List<PostDTO> getPostsByUser(User user);


    Page<PostDTO> searchPostsByTitle(String title, Pageable pageable);
}
