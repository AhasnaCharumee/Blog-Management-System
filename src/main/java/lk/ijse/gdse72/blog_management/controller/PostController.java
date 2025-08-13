package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.service.PostService;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342") // Adjust based on your frontend
public class PostController {
    private final PostService postService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping
    public ResponseEntity<APIResponse<PostDTO>> createPost(
            @RequestPart("post") PostDTO postDTO,
            @RequestPart("image") MultipartFile image) {
        PostDTO createdPost = postService.createPost(postDTO, image);
        return ResponseEntity.ok(new APIResponse<>(200, "Post created successfully", createdPost));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<PostDTO>>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(new APIResponse<>(200, "Posts retrieved successfully", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<PostDTO>> getPostById(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Post retrieved successfully", post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<PostDTO>> updatePost(
            @PathVariable Long id,
            @RequestPart("post") PostDTO postDTO,
            @RequestPart("image") MultipartFile image) {
        PostDTO updatedPost = postService.updatePost(id, postDTO, image);
        return ResponseEntity.ok(new APIResponse<>(200, "Post updated successfully", updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Post deleted successfully", null));
    }
    // src/main/java/lk/ijse/gdse72/blog_management/controller/PostController.java
    @Autowired
    private PostRepository postRepository;


    // src/main/java/lk/ijse/gdse72/blog_management/controller/PostController.java
    @GetMapping("/published")
    public APIResponse<List<PostDTO>> getPublishedPosts() {
        List<Post> publishedPosts = postRepository.findByStatus(PostStatus.APPROVED);
        List<PostDTO> dtos = publishedPosts.stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor(),
                        post.getCreatedDate(),
                        post.getImagePath(),
                        post.getStatus(),
                        post.getViews(),
                        post.getLikes(),
                        post.getCommentsCount()
                ))
                .toList();
        return new APIResponse<>(200, "Success", dtos);
    }
    @GetMapping("/me/posts")
    public List<PostDTO> getMyPosts(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return postService.getPostsByUser(user);
    }
}