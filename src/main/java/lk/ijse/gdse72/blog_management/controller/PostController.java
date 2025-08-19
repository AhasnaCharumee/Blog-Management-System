package lk.ijse.gdse72.blog_management.controller;

import java.io.IOException; // FIXED
import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.dto.CommentDTO;
import lk.ijse.gdse72.blog_management.dto.LikeDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.exceptions.ResourceNotFound;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.repository.CommentRepository;
import lk.ijse.gdse72.blog_management.repository.LikeRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.service.PostService;
import lk.ijse.gdse72.blog_management.service.CommentService;
import lk.ijse.gdse72.blog_management.service.LikeService;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private LikeRepository likeRepository;

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
    public ResponseEntity<APIResponse<List<PostDTO>>> getMyPosts(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));
        }
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFound("User not found with email: " + auth.getName()));
        List<PostDTO> posts = postService.getPostsByUser(user);
        return ResponseEntity.ok(new APIResponse<>(200, "Posts retrieved successfully", posts));
    }

    // Endpoint to toggle a like
    @PostMapping("/{postId}/like")
    public ResponseEntity<APIResponse<LikeDTO>> toggleLike(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));
        }
        String userEmail = authentication.getName();
        LikeDTO result = likeService.toggleLike(postId, userEmail);
        return ResponseEntity.ok(new APIResponse<>(200, "Like toggled successfully", result));
    }

    // Endpoint to get comments for a post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<APIResponse<List<CommentDTO>>> getComments(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(new APIResponse<>(200, "Comments retrieved successfully", comments));
    }

    // Endpoint to create a comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<APIResponse<CommentDTO>> createComment(@PathVariable Long postId, @RequestBody String content, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));
        }
        String userEmail = authentication.getName();
        CommentDTO newComment = commentService.createComment(postId, userEmail, content);
        return ResponseEntity.ok(new APIResponse<>(200, "Comment created successfully", newComment));
    }

    // Endpoint to check if the user has liked a post
    @GetMapping("/{postId}/user-like-status")
    public ResponseEntity<APIResponse<Boolean>> getUserLikeStatus(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            // Not logged in, so they haven't liked it
            return ResponseEntity.ok(new APIResponse<>(200, "User not logged in", false));
        }
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id " + postId));
        boolean hasLiked = user != null && likeRepository.findByUserAndPost(user, post).isPresent();
        return ResponseEntity.ok(new APIResponse<>(200, "Like status retrieved", hasLiked));
    }
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(user.getName());
        post.setCreatedDate(LocalDateTime.now());
        post.setStatus(PostStatus.PENDING); // make sure PENDING exists in enum
        post.setUser(user);

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            try {
                Path uploadPath = Paths.get("uploads");
                if(!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                image.transferTo(uploadPath.resolve(filename));
                post.setImagePath(filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Failed to save image");
            }
        }

        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

}