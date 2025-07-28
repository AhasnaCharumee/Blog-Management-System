package lk.ijse.gdse72.blog_management.service;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, MultipartFile image);
    List<PostDTO> getAllPosts();
    PostDTO getPostById(Long id);
    PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image);
    void deletePost(Long id);
    PostDTO approvePost(Long id);
    PostDTO rejectPost(Long id);
    // In PostService.java
    List<PostDTO> getAllPostsForAdmin();
}