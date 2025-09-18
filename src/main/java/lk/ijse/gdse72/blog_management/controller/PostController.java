package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.dto.CommentDTO;
import lk.ijse.gdse72.blog_management.dto.LikeDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.exceptions.ResourceNotFound;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.repository.LikeRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.service.PostService;
import lk.ijse.gdse72.blog_management.service.CommentService;
import lk.ijse.gdse72.blog_management.service.LikeService;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final LikeRepository likeRepository;

    // ✅ Get all published posts (only APPROVED)
    @GetMapping("/published")
    public APIResponse<List<PostDTO>> getPublishedPosts() {
        List<Post> publishedPosts = postRepository.findByStatus(PostStatus.APPROVED);
        List<PostDTO> dtos = publishedPosts.stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser() != null ? post.getUser().getName() : "Unknown",
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

    // ✅ Get posts of logged-in user (with status info)
    @GetMapping("/me/posts")
    public ResponseEntity<?> getMyPosts(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));
        }

        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new APIResponse<>(404, "User not found", null));
        }

        List<PostDTO> posts = postService.getPostsByUser(userOpt.get());
        return ResponseEntity.ok(new APIResponse<>(200, "Posts retrieved successfully", posts));
    }

    // ✅ Create post (default PENDING)
    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam(value="image", required=false) MultipartFile image,
                                        Authentication auth) {
        if(auth == null || auth.getName() == null)
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedDate(LocalDateTime.now());
        post.setStatus(PostStatus.PENDING); // default PENDING
        post.setUser(user);
        post.setAuthor(user.getName() != null ? user.getName() : user.getEmail());

        // Handle image upload
        if(image != null && !image.isEmpty()){
            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            try {
                Path uploadPath = Paths.get("uploads");
                if(!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                image.transferTo(uploadPath.resolve(filename));
                post.setImagePath(filename);
            } catch (IOException e){
                e.printStackTrace();
                return ResponseEntity.status(500).body(new APIResponse<>(500, "Failed to save image", null));
            }
        }

        postRepository.save(post);
        return ResponseEntity.ok(new APIResponse<>(200, "Post created successfully (Pending Approval)", post));
    }

    // ✅ Update post
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam(value="image", required=false) MultipartFile image) {
        PostDTO dto = new PostDTO();
        dto.setTitle(title);
        dto.setContent(content);

        PostDTO updated = postService.updatePost(id, dto, image);
        return ResponseEntity.ok(new APIResponse<>(200, "Post updated successfully", updated));
    }

    // ✅ Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Post deleted successfully", null));
    }

    // ✅ Toggle like
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, Authentication auth){
        if(auth == null || auth.getName() == null)
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));

        LikeDTO result = likeService.toggleLike(postId, auth.getName());
        return ResponseEntity.ok(new APIResponse<>(200, "Like toggled successfully", result));
    }

    @GetMapping("/{postId}/user-like-status")
    public ResponseEntity<?> getUserLikeStatus(@PathVariable Long postId, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));
        }
        try {
            boolean liked = likeService.hasUserLikedPost(postId, auth.getName());
            return ResponseEntity.ok(new APIResponse<>(200, "Status retrieved", liked));
        } catch (ResourceNotFound ex) {
            return ResponseEntity.status(404).body(new APIResponse<>(404, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new APIResponse<>(500, "Server error", null));
        }
    }

    // ✅ Get comments
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId){
        List<CommentDTO> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(new APIResponse<>(200, "Comments retrieved successfully", comments));
    }

    // ✅ Create comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody String content,
                                           Authentication auth){
        if(auth == null || auth.getName() == null)
            return ResponseEntity.status(401).body(new APIResponse<>(401, "User not authenticated", null));

        CommentDTO comment = commentService.createComment(postId, auth.getName(), content);
        return ResponseEntity.ok(new APIResponse<>(200, "Comment created successfully", comment));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Post> postsPage = postRepository.findByTitleContainingAndStatus(title, PostStatus.APPROVED, pageable);

        Map<String, Object> response = Map.of(
                "content", postsPage.getContent().stream().map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser() != null ? post.getUser().getName() : "Unknown",
                        post.getCreatedDate(),
                        post.getImagePath(),
                        post.getStatus(),
                        post.getViews(),
                        post.getLikes(),
                        post.getCommentsCount()
                )).toList(),
                "totalPages", postsPage.getTotalPages(),
                "number", postsPage.getNumber()
        );

        return ResponseEntity.ok(Map.of("data", response));
    }


    // ✅ Admin: Approve post
    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<?> approvePost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Post not found"));
        post.setStatus(PostStatus.APPROVED);
        post.setCreatedDate(LocalDateTime.now()); // <- Important: set newest date
        postRepository.save(post);
        return ResponseEntity.ok(new APIResponse<>(200, "Post approved successfully", post));
    }


    // ✅ Admin: Reject post
    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<?> rejectPost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Post not found"));
        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);
        return ResponseEntity.ok(new APIResponse<>(200, "Post rejected successfully", post));
    }
}
